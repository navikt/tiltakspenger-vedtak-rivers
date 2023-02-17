package no.nav.tiltakspenger.vedtak.rivers.personopplysninger

import no.nav.tiltakspenger.libs.person.PersonRespons
import java.time.LocalDateTime

data class PersonopplysningerMottattDTO(
    val journalpostId: String,
    val ident: String,
    val personopplysninger: PersonRespons,
    val innhentet: LocalDateTime,
)
