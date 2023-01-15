package no.nav.tiltakspenger.vedtak.rivers

import no.nav.tiltakspenger.libs.arena.tiltak.ArenaTiltaksaktivitetResponsDTO
import java.time.LocalDateTime

data class ArenaTiltakMottattDTO(
    val respons: ArenaTiltaksaktivitetResponsDTO,
    val ident: String,
    val journalpostId: String,
    val innhentet: LocalDateTime
)
