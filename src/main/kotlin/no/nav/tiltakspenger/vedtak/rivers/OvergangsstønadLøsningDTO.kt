package no.nav.tiltakspenger.vedtak.rivers

data class OvergangsstønadLøsningDTO(
    val perioder: List<OvergangsstønadPeriode>,
    val feil: String? = null,
)
