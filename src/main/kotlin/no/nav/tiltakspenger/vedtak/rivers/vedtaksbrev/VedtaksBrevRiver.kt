package no.nav.tiltakspenger.vedtak.rivers.meldekort

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import mu.withLoggingContext
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.tiltakspenger.vedtak.client.IDokumentClient
import no.nav.tiltakspenger.vedtak.rivers.asObject
import no.nav.tiltakspenger.vedtak.rivers.loggEventVedFeil
import no.nav.tiltakspenger.vedtak.rivers.loggEventVedInngang
import no.nav.tiltakspenger.vedtak.rivers.loggEventVedUtgang
import java.time.LocalDate

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

data class BrevDTO(
    val vedtakId: String,
    val vedtaksdato: LocalDate,
    val vedtaksType: VedtaksTypeDTO,
    val periode: PeriodeDTO,
    val saksbehandler: String,
    val beslutter: String,
    val tiltak: List<TiltakDTO>,
)

enum class VedtaksTypeDTO(val navn: String, val skalSendeBrev: Boolean) {
    AVSLAG("Avslag", true),
    INNVILGELSE("Innvilgelse", true),
    STANS("Stans", true),
    FORLENGELSE("Forlengelse", true),
}
