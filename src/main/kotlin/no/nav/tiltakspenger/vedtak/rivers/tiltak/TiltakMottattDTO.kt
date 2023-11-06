package no.nav.tiltakspenger.vedtak.rivers.tiltak

import no.nav.tiltakspenger.libs.tiltak.TiltakResponsDTO
import java.time.LocalDateTime

data class TiltakMottattDTO(
    val respons: TiltakResponsDTO,
    val ident: String,
    val journalpostId: String,
    val innhentet: LocalDateTime,
)
