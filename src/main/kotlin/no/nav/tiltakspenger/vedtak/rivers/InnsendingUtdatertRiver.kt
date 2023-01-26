package no.nav.tiltakspenger.vedtak.rivers

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import mu.withLoggingContext
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.helse.rapids_rivers.asLocalDate
import no.nav.helse.rapids_rivers.asLocalDateTime
import no.nav.tiltakspenger.vedtak.client.IVedtakClient

data class InnsendingUtdatert(val journalpostId: String)

internal class InnsendingUtdatertRiver(
    private val vedtakClient: IVedtakClient,
    rapidsConnection: RapidsConnection
) : River.PacketListener {

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandValue("@event_name", "InnsendingUtdatertHendelse")
                it.requireKey("journalpostId")
                it.requireKey("@opprettet")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        runCatching {
            loggVedInngang("utdatert hendelse", packet)
            withLoggingContext(
                "id" to packet["@id"].asText(),
            ) {
                val innhentet = packet["@opprettet"].asLocalDateTime()
                val journalpostId = packet["journalpostId"].asText()
                val dag = packet["dag"].asLocalDate()

                runBlocking(MDCContext()) {
                    vedtakClient.mottaUtdatert(
                        InnsendingUtdatert(
                            journalpostId = journalpostId,
                        )
                    )
                }
                loggVedUtgang("utdatert hendelse", packet)
            }
        }.onFailure {
            loggVedFeil("utdatert hendelse", it, packet)
        }.getOrThrow()
    }
}
