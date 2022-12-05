@file:Suppress("LongParameterList")

package no.nav.tiltakspenger.vedtak.rivers

import no.nav.tiltakspenger.vedtak.Barnetillegg
import no.nav.tiltakspenger.vedtak.IntroduksjonsprogrammetDetaljer
import no.nav.tiltakspenger.vedtak.Søknad
import no.nav.tiltakspenger.vedtak.Tiltak
import no.nav.tiltakspenger.vedtak.Tiltaksaktivitet
import no.nav.tiltakspenger.vedtak.TrygdOgPensjon
import no.nav.tiltakspenger.vedtak.TypeInstitusjon
import no.nav.tiltakspenger.vedtak.Vedlegg
import no.nav.tiltakspenger.vedtak.rivers.ArenaTiltakDTO.Companion.mapArenatiltak
import no.nav.tiltakspenger.vedtak.rivers.BarnetilleggDTO.Companion.mapBarnetillegg
import no.nav.tiltakspenger.vedtak.rivers.BrukerregistrertTiltakDTO.Companion.mapBrukerregistrertTiltak
import no.nav.tiltakspenger.vedtak.rivers.IntroduksjonsprogrammetDetaljerDTO.Companion.mapIntroduksjonsprogrammetDetaljer
import no.nav.tiltakspenger.vedtak.rivers.TrygdOgPensjonDTO.Companion.mapTrygdOgPensjon
import no.nav.tiltakspenger.vedtak.rivers.VedleggDTO.Companion.mapVedlegg
import java.time.LocalDate
import java.time.LocalDateTime

class SøknadDTO(
    val søknadId: String,
    val journalpostId: String,
    val dokumentInfoId: String,
    val fornavn: String?,
    val etternavn: String?,
    val ident: String,
    val deltarKvp: Boolean,
    val deltarIntroduksjonsprogrammet: Boolean?,
    val introduksjonsprogrammetDetaljer: IntroduksjonsprogrammetDetaljerDTO?,
    val oppholdInstitusjon: Boolean,
    val typeInstitusjon: String?,
    val opprettet: LocalDateTime,
    val barnetillegg: List<BarnetilleggDTO>,
    val arenaTiltak: ArenaTiltakDTO?,
    val brukerregistrertTiltak: BrukerregistrertTiltakDTO?,
    val trygdOgPensjon: List<TrygdOgPensjonDTO>? = emptyList(),
    val fritekst: String?,
    val vedlegg: List<VedleggDTO>? = emptyList(),
) {
    companion object {
        internal fun mapSøknad(dto: SøknadDTO, innhentet: LocalDateTime): Søknad {
            return Søknad(
                søknadId = dto.søknadId,
                journalpostId = dto.journalpostId,
                dokumentInfoId = dto.dokumentInfoId,
                fornavn = dto.fornavn,
                etternavn = dto.etternavn,
                ident = dto.ident,
                deltarKvp = dto.deltarKvp,
                deltarIntroduksjonsprogrammet = dto.deltarIntroduksjonsprogrammet,
                introduksjonsprogrammetDetaljer = mapIntroduksjonsprogrammetDetaljer(dto.introduksjonsprogrammetDetaljer),
                oppholdInstitusjon = dto.oppholdInstitusjon,
                typeInstitusjon = dto.typeInstitusjon?.let { TypeInstitusjonDTO.valueOf(it) }.let {
                    when (it) {
                        TypeInstitusjonDTO.barneverninstitusjon -> TypeInstitusjon.BARNEVERN
                        TypeInstitusjonDTO.overgangsbolig -> TypeInstitusjon.OVERGANGSBOLIG
                        TypeInstitusjonDTO.annet -> TypeInstitusjon.ANNET
                        null -> null
                    }
                },
                opprettet = dto.opprettet,
                barnetillegg = dto.barnetillegg.map { mapBarnetillegg(it) },
                tidsstempelHosOss = innhentet,
                tiltak = mapArenatiltak(dto.arenaTiltak) ?: mapBrukerregistrertTiltak(dto.brukerregistrertTiltak),
                trygdOgPensjon = dto.trygdOgPensjon?.map { mapTrygdOgPensjon(it) } ?: emptyList(),
                fritekst = dto.fritekst,
                vedlegg = dto.vedlegg?.map { mapVedlegg(it) } ?: emptyList(),
            )
        }
    }
}

class BrukerregistrertTiltakDTO(
    val tiltakskode: String,
    val arrangoernavn: String?,
    val beskrivelse: String?,
    val fom: LocalDate,
    val tom: LocalDate,
    val adresse: String? = null,
    val postnummer: String? = null,
    val antallDager: Int
) {
    companion object {
        internal fun mapBrukerregistrertTiltak(dto: BrukerregistrertTiltakDTO?): Tiltak.BrukerregistrertTiltak? =
            if (dto == null) null
            else Tiltak.BrukerregistrertTiltak(
                tiltakskode = Tiltaksaktivitet.mapTiltaksType(dto.tiltakskode), // TODO:test
                arrangoernavn = dto.arrangoernavn,
                beskrivelse = dto.beskrivelse,
                startdato = dto.fom,
                sluttdato = dto.tom,
                adresse = dto.adresse,
                postnummer = dto.postnummer,
                antallDager = dto.antallDager
            )

    }
}

class ArenaTiltakDTO(
    val arenaId: String,
    val arrangoer: String?,
    val harSluttdatoFraArena: Boolean,
    val tiltakskode: String,
    val erIEndreStatus: Boolean,
    val opprinneligSluttdato: LocalDate? = null,
    val opprinneligStartdato: LocalDate,
    val sluttdato: LocalDate? = null,
    val startdato: LocalDate
) {
    companion object {
        internal fun mapArenatiltak(dto: ArenaTiltakDTO?): Tiltak.ArenaTiltak? = if (dto == null) null
        else Tiltak.ArenaTiltak(
            arenaId = dto.arenaId,
            arrangoernavn = dto.arrangoer,
            harSluttdatoFraArena = dto.harSluttdatoFraArena,
            tiltakskode = Tiltaksaktivitet.Tiltak.valueOf(dto.tiltakskode.uppercase()),  // TODO test this
            erIEndreStatus = dto.erIEndreStatus,
            opprinneligSluttdato = dto.opprinneligSluttdato,
            opprinneligStartdato = dto.opprinneligStartdato,
            sluttdato = dto.sluttdato,
            startdato = dto.startdato
        )
    }
}

class IntroduksjonsprogrammetDetaljerDTO(
    val fom: LocalDate,
    val tom: LocalDate? = null,
) {
    companion object {
        internal fun mapIntroduksjonsprogrammetDetaljer(dto: IntroduksjonsprogrammetDetaljerDTO?) =
            dto?.let { IntroduksjonsprogrammetDetaljer(fom = dto.fom, tom = dto.tom) }
    }
}

class TrygdOgPensjonDTO(
    val utbetaler: String, val prosent: Int? = null, val fom: LocalDate? = null, val tom: LocalDate? = null
) {
    companion object {
        internal fun mapTrygdOgPensjon(dto: TrygdOgPensjonDTO): TrygdOgPensjon = TrygdOgPensjon(
            utbetaler = dto.utbetaler, prosent = dto.prosent, fom = dto.fom, tom = dto.tom
        )
    }
}

data class VedleggDTO(
    val journalpostId: String,
    val dokumentInfoId: String,
    val filnavn: String?,
) {
    companion object {
        internal fun mapVedlegg(dto: VedleggDTO): Vedlegg {
            return Vedlegg(
                journalpostId = dto.journalpostId,
                dokumentInfoId = dto.dokumentInfoId,
                filnavn = dto.filnavn,
            )
        }
    }
}

class BarnetilleggDTO(
    val alder: Int,
    val oppholdsland: String,
    val ident: String? = null,
    val fødselsdato: LocalDate? = null,
    val fornavn: String? = null,
    val mellomnavn: String? = null,
    val etternavn: String? = null,
    val søktBarnetillegg: Boolean? = null, //Er midlertidig at det er null, endres når alt er i sync
) {
    companion object {
        internal fun mapBarnetillegg(dto: BarnetilleggDTO): Barnetillegg {
            return if (dto.ident != null) Barnetillegg.MedIdent(
                alder = dto.alder,
                oppholdsland = dto.oppholdsland,
                ident = dto.ident,
                fornavn = dto.fornavn,
                mellomnavn = dto.mellomnavn,
                etternavn = dto.etternavn,
                søktBarnetillegg = dto.søktBarnetillegg ?: true,
            ) else
                Barnetillegg.UtenIdent(
                    alder = dto.alder,
                    oppholdsland = dto.oppholdsland,
                    fødselsdato = dto.fødselsdato!!,
                    fornavn = dto.fornavn,
                    mellomnavn = dto.mellomnavn,
                    etternavn = dto.etternavn,
                    søktBarnetillegg = dto.søktBarnetillegg ?: true
                )
        }
    }
}

enum class TypeInstitusjonDTO(val type: String) {
    barneverninstitusjon("barneverninstitusjon"),
    overgangsbolig("overgangsbolig"),
    annet("annet"),
}
