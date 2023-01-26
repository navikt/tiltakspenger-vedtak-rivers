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

internal class OvergangsstønadMottattRiver(
    private val vedtakClient: IVedtakClient,
    rapidsConnection: RapidsConnection,
) : River.PacketListener {

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandAllOrAny("@behov", listOf("overgangsstønad"))
                it.demandKey("@løsning")
                it.demandKey("@id")
                it.demandKey("@behovId")
                it.requireKey("ident")
                it.requireKey("journalpostId")
                it.requireKey("@opprettet")
                it.interestedIn("@løsning.perioder")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        runCatching {
            loggVedInngang("overgangsstønad", packet)
            withLoggingContext(
                "id" to packet["@id"].asText(),
                "behovId" to packet["@behovId"].asText(),
            ) {
                val ident = packet["ident"].asText()
                val behovId = packet["@behovId"].asText()
                val innhentet = packet["@opprettet"].asLocalDateTime()
                val journalpostId = packet["journalpostId"].asText()
                val perioder = packet["@løsning.perioder"].asList<OvergangsstønadPeriode>()

                runBlocking(MDCContext()) {
                    vedtakClient.mottaOvergangsstønad(
                        overgangsstønadDTO = OvergangsstønadDTO(
                            ident = ident,
                            perioder = perioder,
                            innhentet = innhentet,
                            journalpostId = journalpostId,
                        ),
                        behovId = behovId,
                    )
                }
                loggVedUtgang("overgangsstønad", packet)
            }
        }.onFailure {
            loggVedFeil("overgangsstønad", it, packet)
        }.getOrThrow()
    }
}
