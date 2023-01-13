package no.nav.tiltakspenger.vedtak.rivers

import no.nav.tiltakspenger.libs.person.PersonRespons
import java.time.LocalDateTime

data class PersonopplysningerMottattDTO(
    val journalpostId: String,
    val ident: String,
    val personopplysninger: PersonRespons,
    val innhentet: LocalDateTime,
)
