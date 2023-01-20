package no.nav.tiltakspenger.vedtak.rivers

import no.nav.tiltakspenger.libs.skjerming.SkjermingResponsDTO
import java.time.LocalDateTime

data class SkjermingDTO(
    val ident: String,
    val journalpostId: String,
    val skjerming: SkjermingResponsDTO,
    val innhentet: LocalDateTime,
)
