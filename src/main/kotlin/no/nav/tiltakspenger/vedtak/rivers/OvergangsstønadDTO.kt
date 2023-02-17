package no.nav.tiltakspenger.vedtak.rivers

import no.nav.tiltakspenger.libs.overgangsstonad.OvergangsstønadResponsDTO
import java.time.LocalDateTime

data class OvergangsstønadDTO(
    val ident: String,
    val overgangsstønadRespons: OvergangsstønadResponsDTO,
    val journalpostId: String,
    val innhentet: LocalDateTime,
)
