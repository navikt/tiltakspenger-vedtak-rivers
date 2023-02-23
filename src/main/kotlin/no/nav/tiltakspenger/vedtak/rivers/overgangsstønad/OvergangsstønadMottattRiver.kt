package no.nav.tiltakspenger.vedtak.rivers.overgangsstønad

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import mu.withLoggingContext
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.helse.rapids_rivers.asLocalDateTime
import no.nav.tiltakspenger.libs.overgangsstonad.OvergangsstønadResponsDTO
import no.nav.tiltakspenger.vedtak.client.IVedtakClient
import no.nav.tiltakspenger.vedtak.rivers.asObject
import no.nav.tiltakspenger.vedtak.rivers.loggBehovVedFeil
import no.nav.tiltakspenger.vedtak.rivers.loggBehovVedInngang
import no.nav.tiltakspenger.vedtak.rivers.loggBehovVedUtgang

internal class OvergangsstønadMottattRiver(
    private val vedtakClient: IVedtakClient,
    rapidsConnection: RapidsConnection,
) : River.PacketListener {

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandAllOrAny("@behov", listOf("overgangsstønad"))
                it.demandKey("@løsning")
                it.demandKey("@løsning.overgangsstønad")
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
            loggBehovVedInngang("overgangsstønad", packet)
            withLoggingContext(
                "id" to packet["@id"].asText(),
                "behovId" to packet["@behovId"].asText(),
            ) {
                val ident = packet["ident"].asText()
                val behovId = packet["@behovId"].asText()
                val innhentet = packet["@opprettet"].asLocalDateTime()
                val journalpostId = packet["journalpostId"].asText()
                val overgangsstønadData =
                    packet["@løsning.overgangsstønad"].asObject(OvergangsstønadResponsDTO::class.java)

                runBlocking(MDCContext()) {
                    vedtakClient.mottaOvergangsstønad(
                        overgangsstønadDTO = OvergangsstønadDTO(
                            ident = ident,
                            overgangsstønadRespons = overgangsstønadData,
                            innhentet = innhentet,
                            journalpostId = journalpostId,
                        ),
                        behovId = behovId,
                    )
                }
                loggBehovVedUtgang("overgangsstønad", packet)
            }
        }.onFailure {
            loggBehovVedFeil("overgangsstønad", it, packet)
        }.getOrThrow()
    }
}
