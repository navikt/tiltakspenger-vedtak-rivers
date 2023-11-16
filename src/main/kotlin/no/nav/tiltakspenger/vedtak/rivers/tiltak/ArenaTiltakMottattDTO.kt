package no.nav.tiltakspenger.vedtak.rivers.tiltak

import no.nav.tiltakspenger.libs.tiltak.TiltakResponsDTO
import java.time.LocalDate
import java.time.LocalDateTime

data class ArenaTiltakMottattDTO(
    val respons: ArenaTiltaksaktivitetResponsDTO,
    val ident: String,
    val journalpostId: String,
    val innhentet: LocalDateTime,
)

data class ArenaTiltaksaktivitetResponsDTO(
    val tiltaksaktiviteter: List<TiltaksaktivitetDTO>? = null,
    val feil: FeilmeldingDTO? = null,
) {

    enum class FeilmeldingDTO(val melding: String) {
        UkjentFeil("Ukjent feil"),
    }

    data class TiltaksaktivitetDTO(
        val tiltakType: TiltakResponsDTO.TiltakType,
        val aktivitetId: String,
        val tiltakLokaltNavn: String?,
        val arrangoer: String?,
        val bedriftsnummer: String?,
        val deltakelsePeriode: DeltakelsesPeriodeDTO?,
        val deltakelseProsent: Float?,
        val deltakerStatusType: DeltakerStatusType,
        val statusSistEndret: LocalDate?,
        val begrunnelseInnsoeking: String?,
        val antallDagerPerUke: Float?,
    )

    data class DeltakelsesPeriodeDTO(
        val fom: LocalDate?,
        val tom: LocalDate?,
    )

    enum class DeltakerStatusType(val navn: String) {
        AKTUELL("Aktuell"),
        AVSLAG("Fått avslag"),
        DELAVB("Deltakelse avbrutt"),
        FULLF("Fullført"),
        GJENN("Gjennomføres"),
        GJENN_AVB("Gjennomføring avbrutt"),
        GJENN_AVL("Gjennomføring avlyst"),
        IKKAKTUELL("Ikke aktuell"),
        IKKEM("Ikke møtt"),
        INFOMOETE("Informasjonsmøte"),
        JATAKK("Takket ja til tilbud"),
        NEITAKK("Takket nei til tilbud"),
        TILBUD("Godkjent tiltaksplass"),
        VENTELISTE("Venteliste"),
    }
}
