package no.nav.tiltakspenger.vedtak.rivers.søknad

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import no.nav.tiltakspenger.vedtak.client.IVedtakClient
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.time.LocalDateTime

internal class SøknadMottattRiverTest {

    private val vedtakClient = mockk<IVedtakClient>()
    private val testRapid = TestRapid()

    init {
        SøknadMottattRiver(
            vedtakClient = vedtakClient,
            rapidsConnection = testRapid,
        )
    }

    @BeforeEach
    fun reset() {
        testRapid.reset()
    }

    @Test
    fun `Når en søknad mottas, så videresender vi data til tiltakspenger-vedtak`() {
        coEvery { vedtakClient.mottaSøknad(any(), any()) } returns Unit
        testRapid.sendTestMessage(søknad())
        coVerify { vedtakClient.mottaSøknad(søknadDTO(), "journalpostId1") }
    }

    @Test
    fun `Når tiltakspenger-vedtak returnerer en feil, så skal vi kræsje`() {
        coEvery { vedtakClient.mottaSøknad(any(), any()) } throws RuntimeException("Feil")
        assertThrows<RuntimeException> {
            testRapid.sendTestMessage(søknad())
        }
        coVerify { vedtakClient.mottaSøknad(søknadDTO(), "journalpostId1") }
    }

    private fun søknad(): String =
        """
        {
          "@event_name": "søknad_mottatt",
          "søknad": {
            "versjon": "1",
            "søknadId": "12304",
            "dokInfo": {
              "journalpostId": "journalpostId1",
              "dokumentInfoId": "43",
              "filnavn": "tiltakspenger.json"
            },
            "personopplysninger": {
              "ident": "26037802335",
              "fornavn": "TALENTFULL",
              "etternavn": "GYNGEHEST"
            },
            "tiltak": {
                "id": "id",
                "deltakelseFom": "2022-02-01",
                "deltakelseTom": "2022-02-28",
                "arrangør": "arrangør",
                "typeKode": "AMO",
                "typeNavn": "AMO"
            },
            "barnetilleggPdl": [],
            "barnetilleggManuelle": [],
            "vedlegg": [],
            "kvp": {
              "svar": "Nei",
              "fom": null,
              "tom": null
            },
            "intro": {
              "svar": "IkkeBesvart",
              "fom": null,
              "tom": null
            },
            "institusjon": {
              "svar": "Nei",
              "fom": null,
              "tom": null
            },
            "etterlønn": {
              "svar": "Nei"
            },
            "gjenlevendepensjon": {
              "svar": "IkkeMedISøknaden",
              "fom": null
            },
            "alderspensjon": {
              "svar": "IkkeMedISøknaden",
              "fom": null
            },
            "sykepenger": {
              "svar": "IkkeMedISøknaden",
              "fom": null,
              "tom": null
            },
            "supplerendeStønadAlder": {
              "svar": "IkkeMedISøknaden",
              "fom": null,
              "tom": null
            },
            "supplerendeStønadFlyktning": {
              "svar": "IkkeMedISøknaden",
              "fom": null,
              "tom": null
            },
            "jobbsjansen": {
              "svar": "IkkeMedISøknaden",
              "fom": null,
              "tom": null
            },
            "trygdOgPensjon": {
              "svar": "IkkeMedISøknaden",
              "fom": null,
              "tom": null
            },
            "opprettet": "2022-02-08T14:26:42.000000831"
          },
          "@id": "369bf01c-f46f-4cb9-ba0d-01beb0905edc",
          "@opprettet": "2022-06-29T16:25:33.598375671",
          "system_read_count": 1,
          "system_participating_services": [
            {
              "id": "369bf01c-f46f-4cb9-ba0d-01beb0905edc",
              "time": "2022-06-29T16:25:33.598375671",
              "service": "tiltakspenger-mottak",
              "instance": "tiltakspenger-mottak-6c65db7887-ffwcv",
              "image": "ghcr.io/navikt/tiltakspenger-mottak:2074ee7461ad748d7c99d26ee5b7374e0c7fd9f4"
            }
          ]
        }
        """.trimIndent()

    private fun søknadDTO(
        versjon: String = "1",
        søknadId: String = "12304",
        dokInfo: DokumentInfoDTO = DokumentInfoDTO(
            journalpostId = "journalpostId1",
            dokumentInfoId = "43",
            filnavn = "tiltakspenger.json",
        ),
        personopplysninger: PersonopplysningerDTO = PersonopplysningerDTO(
            fornavn = "TALENTFULL",
            etternavn = "GYNGEHEST",
            ident = "26037802335",
        ),
        kvp: PeriodeSpmDTO = PeriodeSpmDTO(svar = SpmSvarDTO.Nei, fom = null, tom = null),
        intro: PeriodeSpmDTO = PeriodeSpmDTO(svar = SpmSvarDTO.IkkeBesvart, fom = null, tom = null),
        institusjon: PeriodeSpmDTO = PeriodeSpmDTO(svar = SpmSvarDTO.Nei, fom = null, tom = null),
        barnetilleggPdl: List<BarnetilleggDTO> = emptyList(),
        barnetilleggManuelle: List<BarnetilleggDTO> = emptyList(),
        opprettet: LocalDateTime = LocalDateTime.of(2022, 2, 8, 14, 26, 42, 831),
        tiltak: TiltakDTO = TiltakDTO(
            id = "id",
            deltakelseFom = LocalDate.of(2022, 2, 1),
            deltakelseTom = LocalDate.of(2022, 2, 28),
            arrangør = "arrangør",
            typeKode = "AMO",
            typeNavn = "AMO",
        ),
        alderspensjon: FraOgMedDatoSpmDTO = FraOgMedDatoSpmDTO(svar = SpmSvarDTO.IkkeMedISøknaden, fom = null),
        etterlønn: JaNeiSpmDTO = JaNeiSpmDTO(SpmSvarDTO.Nei),
        gjenlevendePensjon: PeriodeSpmDTO = PeriodeSpmDTO(svar = SpmSvarDTO.IkkeMedISøknaden, fom = null, tom = null),
        jobbsjansen: PeriodeSpmDTO = PeriodeSpmDTO(svar = SpmSvarDTO.IkkeMedISøknaden, fom = null, tom = null),
        supplerendeAlder: PeriodeSpmDTO = PeriodeSpmDTO(svar = SpmSvarDTO.IkkeMedISøknaden, fom = null, tom = null),
        supplerendeFlykting: PeriodeSpmDTO = PeriodeSpmDTO(svar = SpmSvarDTO.IkkeMedISøknaden, fom = null, tom = null),
        sykepenger: PeriodeSpmDTO = PeriodeSpmDTO(svar = SpmSvarDTO.IkkeMedISøknaden, fom = null, tom = null),
        trygdOgPensjon: PeriodeSpmDTO = PeriodeSpmDTO(svar = SpmSvarDTO.IkkeMedISøknaden, fom = null, tom = null),
        vedlegg: List<DokumentInfoDTO> = emptyList(),
    ) = SøknadDTO(
        versjon = versjon,
        søknadId = søknadId,
        dokInfo = dokInfo,
        personopplysninger = personopplysninger,
        kvp = kvp,
        intro = intro,
        institusjon = institusjon,
        barnetilleggPdl = barnetilleggPdl,
        barnetilleggManuelle = barnetilleggManuelle,
        tiltak = tiltak,
        alderspensjon = alderspensjon,
        etterlønn = etterlønn,
        gjenlevendepensjon = gjenlevendePensjon,
        jobbsjansen = jobbsjansen,
        supplerendeStønadAlder = supplerendeAlder,
        supplerendeStønadFlyktning = supplerendeFlykting,
        sykepenger = sykepenger,
        trygdOgPensjon = trygdOgPensjon,
        opprettet = opprettet,
        vedlegg = vedlegg,
    )
}
