package no.nav.tiltakspenger.vedtak.rivers.uføre

import no.nav.tiltakspenger.libs.ufore.UforeResponsDTO
import java.time.LocalDateTime

data class UføreDTO(
    val ident: String,
    val journalpostId: String,
    val uføre: UforeResponsDTO,
    val innhentet: LocalDateTime,
)
