package no.nav.tiltakspenger.vedtak.rivers.vedtaksbrev

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import mu.withLoggingContext
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.tiltakspenger.libs.dokument.BrevDTO
import no.nav.tiltakspenger.vedtak.client.IDokumentClient
import no.nav.tiltakspenger.vedtak.rivers.asObject
import no.nav.tiltakspenger.vedtak.rivers.loggEventVedFeil
import no.nav.tiltakspenger.vedtak.rivers.loggEventVedInngang
import no.nav.tiltakspenger.vedtak.rivers.loggEventVedUtgang

internal class VedtaksBrevRiver(
    private val dokumentClient: IDokumentClient,
    rapidsConnection: RapidsConnection,
) : River.PacketListener {

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandValue("@event_name", "vedtaksbrev")
                it.demandKey("@id")
                it.requireKey("vedtaksbrev")
                it.requireKey("@opprettet")
                it.requireKey("@id")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        runCatching {
            loggEventVedInngang("vedtaksbrev", packet)
            withLoggingContext(
                "id" to packet["@id"].asText(),
            ) {
                val dto = packet["vedtaksbrev"].asObject(BrevDTO::class.java)

                runBlocking(MDCContext()) {
                    dokumentClient.mottaVedtaksBrev(dto)
                }
                loggEventVedUtgang("vedtaksbrev", packet)
            }
        }.onFailure {
            loggEventVedFeil("vedtaksbrev", it, packet)
        }.getOrThrow()
    }
}
