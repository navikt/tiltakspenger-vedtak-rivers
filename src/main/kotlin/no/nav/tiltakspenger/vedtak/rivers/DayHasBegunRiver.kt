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
import java.time.LocalDate

data class DayHasBegunEvent(val date: LocalDate)

internal class DayHasBegunRiver(
    private val vedtakClient: IVedtakClient,
    rapidsConnection: RapidsConnection,
) : River.PacketListener {

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandValue("@event_name", "hourHasBegunEvent")
                it.requireKey("hourHasBegun")
                it.requireKey("@opprettet")
                it.requireKey("@id")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        runCatching {
            loggEventVedInngang("dayHasBegun", packet)
            withLoggingContext(
                "id" to packet["@id"].asText(),
            ) {
                val innhentet = packet["@opprettet"].asLocalDateTime()
                val dag = packet["hourHasBegun"].asLocalDateTime().toLocalDate()

                runBlocking(MDCContext()) {
                    vedtakClient.mottaDayHasBegun(
                        DayHasBegunEvent(date = dag),
                    )
                }
                loggEventVedUtgang("dayHasBegun", packet)
            }
        }.onFailure {
            loggEventVedFeil("dayHasBegun", it, packet)
        }.getOrThrow()
    }
}
