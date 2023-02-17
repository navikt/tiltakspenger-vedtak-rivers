package no.nav.tiltakspenger.vedtak.rivers.ytelser

import no.nav.tiltakspenger.libs.arena.ytelse.ArenaYtelseResponsDTO
import java.time.LocalDateTime

data class ArenaYtelserMottattDTO(
    val respons: ArenaYtelseResponsDTO,
    val ident: String,
    val journalpostId: String,
    val innhentet: LocalDateTime,
)
