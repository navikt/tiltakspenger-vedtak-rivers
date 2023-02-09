package no.nav.tiltakspenger.vedtak.rivers

import java.time.LocalDateTime

data class OvergangsstønadDTO(
    val ident: String,
    val perioder: List<OvergangsstønadPeriode>,
    val journalpostId: String,
    val innhentet: LocalDateTime,
)
