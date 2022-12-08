package no.nav.tiltakspenger.vedtak.rivers

import java.time.LocalDateTime

data class ArenaTiltakMottattDTO(
    val tiltak: List<TiltaksaktivitetDTO>?,
    val ident: String,
    val journalpostId: String,
    val innhentet: LocalDateTime,
    val feil: String?
)
