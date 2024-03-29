package no.nav.tiltakspenger.vedtak.rivers.meldekort

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import mu.withLoggingContext
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.tiltakspenger.vedtak.client.IMeldekortClient
import no.nav.tiltakspenger.vedtak.rivers.asObject
import no.nav.tiltakspenger.vedtak.rivers.loggEventVedFeil
import no.nav.tiltakspenger.vedtak.rivers.loggEventVedInngang
import no.nav.tiltakspenger.vedtak.rivers.loggEventVedUtgang
import java.time.LocalDate

internal class MeldekortGrunnlagRiver(
    private val meldekortClient: IMeldekortClient,
    rapidsConnection: RapidsConnection,
) : River.PacketListener {

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandValue("@event_name", "meldekortGrunnlag")
                it.demandKey("@id")
                it.requireKey("meldekortGrunnlag")
                it.requireKey("@opprettet")
                it.requireKey("@id")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        runCatching {
            loggEventVedInngang("meldekortGrunnlag", packet)
            withLoggingContext(
                "id" to packet["@id"].asText(),
            ) {
                val dto = packet["meldekortGrunnlag"].asObject(MeldekortGrunnlagDTO::class.java)

                runBlocking(MDCContext()) {
                    meldekortClient.mottaMeldekortGrunnlag(dto)
                }
                loggEventVedUtgang("meldekortGrunnlag", packet)
            }
        }.onFailure {
            loggEventVedFeil("meldekortGrunnlag", it, packet)
        }.getOrThrow()
    }
}

data class MeldekortGrunnlagDTO(
    val vedtakId: String,
    val sakId: String,
    val behandlingId: String,
    val status: StatusDTO,
    val vurderingsperiode: PeriodeDTO,
    val tiltak: List<TiltakDTO>,
    val personopplysninger: PersonopplysningerDTO,
    val utfallsperioder: List<UtfallsperiodeDTO>,
)

data class UtfallsperiodeDTO(
    val fom: LocalDate,
    val tom: LocalDate,
    val antallBarn: Int,
    val utfall: UtfallForPeriodeDTO,
)

enum class UtfallForPeriodeDTO {
    GIR_RETT_TILTAKSPENGER,
    GIR_IKKE_RETT_TILTAKSPENGER,
    KREVER_MANUELL_VURDERING,
}

data class PersonopplysningerDTO(
    val fornavn: String,
    val etternavn: String,
    val ident: String,
)

enum class StatusDTO {
    AKTIV,
    IKKE_AKTIV,
}

data class TiltakDTO(
    val periodeDTO: PeriodeDTO,
    val typeBeskrivelse: String,
    val typeKode: String,
    val antDagerIUken: Float,
)
data class PeriodeDTO(
    val fra: LocalDate,
    val til: LocalDate,
)
