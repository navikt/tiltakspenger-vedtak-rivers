package no.nav.tiltakspenger.vedtak.rivers.foreldrepenger

import no.nav.tiltakspenger.libs.fp.FPResponsDTO
import java.time.LocalDateTime

data class ForeldrepengerDTO(
    val ident: String,
    val journalpostId: String,
    val foreldrepenger: FPResponsDTO,
    val innhentet: LocalDateTime,
)
