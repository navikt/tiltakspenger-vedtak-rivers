package no.nav.tiltakspenger.vedtak.rivers

import no.nav.tiltakspenger.libs.fp.FPResponsDTO
import java.time.LocalDateTime

data class ForeldrepengerDTO(
    val ident: String,
    val journalpostId: String,
    val skjerming: FPResponsDTO,
    val innhentet: LocalDateTime,
)
