package no.nav.tiltakspenger.vedtak.rivers.søknad

import java.time.LocalDate

data class TiltakDTO(
    val id: String,
    val deltakelseFom: LocalDate,
    val deltakelseTom: LocalDate,
    val arrangør: String,
    val typeKode: String,
    val typeNavn: String,
)
