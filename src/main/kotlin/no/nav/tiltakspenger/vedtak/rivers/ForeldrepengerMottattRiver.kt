package no.nav.tiltakspenger.vedtak.rivers

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import mu.withLoggingContext
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.helse.rapids_rivers.asLocalDateTime
import no.nav.tiltakspenger.libs.fp.FPResponsDTO
import no.nav.tiltakspenger.vedtak.client.IVedtakClient

internal class ForeldrepengerMottattRiver(
    private val vedtakClient: IVedtakClient,
    rapidsConnection: RapidsConnection,
) : River.PacketListener {

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandAllOrAny("@behov", listOf("fpytelser"))
                it.demandKey("@løsning")
                it.demandKey("@løsning.fpytelser")
                it.demandKey("@id")
                it.demandKey("@behovId")
                it.requireKey("ident")
                it.requireKey("journalpostId")
                it.requireKey("@opprettet")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        runCatching {
            loggBehovVedInngang("fpytelser", packet)
            withLoggingContext(
                "id" to packet["@id"].asText(),
                "behovId" to packet["@behovId"].asText(),
            ) {
                val ident = packet["ident"].asText()
                val behovId = packet["@behovId"].asText()
                val innhentet = packet["@opprettet"].asLocalDateTime()
                val journalpostId = packet["journalpostId"].asText()
                val dto = if (packet["@løsning.fpytelser"].isEmpty) {
                    FPResponsDTO(
                        ytelser = emptyList(),
                        feil = null,
                    )
                } else {
                    packet["@løsning.fpytelser"].asObject(FPResponsDTO::class.java)
                }

                runBlocking(MDCContext()) {
                    vedtakClient.mottaForeldrepenger(
                        foreldrepengerDTO = ForeldrepengerDTO(
                            ident = ident,
                            journalpostId = journalpostId,
                            foreldrepenger = dto,
                            innhentet = innhentet,
                        ),
                        behovId = behovId,
                    )
                }
                loggBehovVedUtgang("fpytelser", packet)
            }
        }.onFailure {
            loggBehovVedFeil("fpytelser", it, packet)
        }.getOrThrow()
    }
}
