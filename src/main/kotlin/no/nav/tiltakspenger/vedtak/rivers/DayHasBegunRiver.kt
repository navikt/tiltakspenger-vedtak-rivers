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
import java.time.LocalDate

data class DayHasBegunEvent(val date: LocalDate)

internal class DayHasBegunRiver(
    private val vedtakClient: IVedtakClient,
    rapidsConnection: RapidsConnection
) : River.PacketListener {

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandValue("@event_name", "dayHasBegunEvent")
                it.requireKey("dayHasBegun")
                it.requireKey("@opprettet")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        runCatching {
            loggVedInngang("dayHasBegun", packet)
            withLoggingContext(
                "id" to packet["@id"].asText(),
            ) {
                val innhentet = packet["@opprettet"].asLocalDateTime()
                val dag = packet["dayHasBegun"].asLocalDate()

                runBlocking(MDCContext()) {
                    vedtakClient.mottaDayHasBegun(
                        DayHasBegunEvent(date = dag)
                    )
                }
                loggVedUtgang("dayHasBegun", packet)
            }
        }.onFailure {
            loggVedFeil("dayHasBegun", it, packet)
        }.getOrThrow()
    }
}