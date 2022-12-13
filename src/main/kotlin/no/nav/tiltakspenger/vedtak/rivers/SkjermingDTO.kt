package no.nav.tiltakspenger.vedtak.rivers

import java.time.LocalDateTime

data class SkjermingDTO(
    val ident: String,
    val journalpostId: String,
    val skjerming: Boolean,
    val innhentet: LocalDateTime,
)
