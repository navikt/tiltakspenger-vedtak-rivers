package no.nav.tiltakspenger.vedtak.rivers

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import mu.withLoggingContext
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.helse.rapids_rivers.asLocalDateTime
import no.nav.tiltakspenger.vedtak.client.IVedtakClient

internal class SkjermingMottattRiver(
    private val vedtakClient: IVedtakClient,
    rapidsConnection: RapidsConnection
) : River.PacketListener {

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandAllOrAny("@behov", listOf("skjerming"))
                it.demandKey("@løsning")
                it.demandKey("@id")
                it.demandKey("@behovId")
                it.requireKey("ident")
                it.requireKey("journalpostId")
                it.requireKey("@opprettet")
                it.interestedIn("@løsning.skjerming")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        runCatching {
            loggVedInngang("skjerming", packet)
            withLoggingContext(
                "id" to packet["@id"].asText(),
                "behovId" to packet["@behovId"].asText()
            ) {
                val ident = packet["ident"].asText()
                val behovId = packet["@behovId"].asText()
                val innhentet = packet["@opprettet"].asLocalDateTime()
                val skjerming = packet["@løsning.skjerming"].asBoolean()
                val journalpostId = packet["journalpostId"].asText()

                runBlocking(MDCContext()) {
                    vedtakClient.mottaSkjerming(
                        skjermingDTO = SkjermingDTO(
                            ident = ident,
                            journalpostId = journalpostId,
                            skjerming = skjerming,
                            innhentet =  innhentet,
                        ),
                        behovId = behovId
                    )
                }
                loggVedUtgang("skjerming", packet)
            }
        }.onFailure {
            loggVedFeil("skjerming", it, packet)
        }.getOrThrow()
    }
}
