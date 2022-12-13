package no.nav.tiltakspenger.vedtak.rivers

import java.time.LocalDateTime

data class ArenaYtelserMottattDTO(
    val ytelser: List<YtelseSakDTO>?,
    val ident: String,
    val journalpostId: String,
    val innhentet: LocalDateTime,
)