package no.nav.tiltakspenger.vedtak.rivers.søknad

import java.time.LocalDate

data class TiltakDTO(
    val aktivitetId: String,
    val periode: Periode,
    val arenaRegistrertPeriode: Deltakelsesperiode?,
    val arrangør: String,
    val type: String,
    val typeNavn: String,
)

data class Periode(
    val fra: LocalDate,
    val til: LocalDate,
)

data class Deltakelsesperiode(
    val fra: LocalDate?,
    val til: LocalDate?,
)
