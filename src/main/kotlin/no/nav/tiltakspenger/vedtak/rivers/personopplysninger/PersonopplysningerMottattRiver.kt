package no.nav.tiltakspenger.vedtak.rivers.personopplysninger

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import mu.withLoggingContext
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.helse.rapids_rivers.asLocalDateTime
import no.nav.tiltakspenger.libs.person.PersonRespons
import no.nav.tiltakspenger.vedtak.client.IVedtakClient
import no.nav.tiltakspenger.vedtak.rivers.asObject
import no.nav.tiltakspenger.vedtak.rivers.loggFeltVedFeil
import no.nav.tiltakspenger.vedtak.rivers.loggFeltVedInngang
import no.nav.tiltakspenger.vedtak.rivers.loggFeltVedUtgang

internal class PersonopplysningerMottattRiver(
    private val vedtakClient: IVedtakClient,
    rapidsConnection: RapidsConnection,
) : River.PacketListener {

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandAllOrAny("@behov", listOf("personopplysninger"))
                it.demandKey("@løsning")
                it.demandKey("@løsning.personopplysninger")
                it.demandKey("@id")
                it.demandKey("@behovId")
                it.requireKey("ident")
                it.requireKey("journalpostId")
                it.requireKey("@opprettet")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        val ident = packet["ident"].asText()
        runCatching {
            loggFeltVedInngang("personopplysninger", "fnr", ident)
            withLoggingContext(
                "id" to packet["@id"].asText(),
                "behovId" to packet["@behovId"].asText(),
            ) {
                val behovId = packet["@behovId"].asText()
                val journalpostId = packet["journalpostId"].asText()
                val innhentet = packet["@opprettet"].asLocalDateTime()
                val dto = packet["@løsning.personopplysninger"].asObject(PersonRespons::class.java)

                runBlocking(MDCContext()) {
                    vedtakClient.mottaPersonopplysninger(
                        PersonopplysningerMottattDTO(
                            journalpostId = journalpostId,
                            ident = ident,
                            personopplysninger = dto,
                            innhentet = innhentet,
                        ),
                        behovId = behovId,
                    )
                }
                loggFeltVedUtgang("personopplysninger", "fnr", ident)
            }
        }.onFailure {
            loggFeltVedFeil("personopplysninger", it, "fnr", ident)
        }.getOrThrow()
    }
}
