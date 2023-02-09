package no.nav.tiltakspenger.vedtak.rivers

data class OvergangsstønadLøsningDTO(
    val ident: String,
    val perioder: List<OvergangsstønadPeriode>,
    val journalpostId: String,
    val feil: String? = null,
)
