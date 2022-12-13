package no.nav.tiltakspenger.vedtak.rivers

import java.time.LocalDateTime

data class PersonopplysningerMottattDTO(
    val journalpostId: String,
    val ident: String,
    val personopplysninger: PersonopplysningerDTO,
    val innhentet: LocalDateTime,
)