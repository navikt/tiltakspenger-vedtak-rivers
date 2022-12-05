package no.nav.tiltakspenger.vedtak.rivers

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.spyk
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import no.nav.tiltakspenger.vedtak.client.VedtakClient
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled
internal class SøknadMottattRiverTest {

    private val vedtakClient = spyk<VedtakClient>()
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
        testRapid.sendTestMessage(søknad())
        coEvery { vedtakClient.mottaTiltak(any(), any()) } returns Unit
        coVerify { vedtakClient.mottaTiltak(any(), any()) }
    }

    private fun søknad(): String =
        """
        {
          "@event_name": "søknad_mottatt",
          "søknad": {
            "søknadId": "whatever",
            "journalpostId": "journalpostId1",
            "dokumentInfoId": "whatever3",
            "id": "13306",
            "fornavn": "LEVENDE",
            "etternavn": "POTET",
            "ident": "ident1",
            "deltarKvp": false,
            "deltarIntroduksjonsprogrammet": false,
            "oppholdInstitusjon": false,
            "typeInstitusjon": null,
            "tiltaksArrangoer": "foo",
            "tiltaksType": "JOBSOK",
            "arenaTiltak" : {
                 "arenaId" : "id",
                 "arrangoer" : "navn",
                 "harSluttdatoFraArena" : false,
                 "tiltakskode" : "MENTOR",
                 "erIEndreStatus" : false,
                 "opprinneligSluttdato": null,
                 "opprinneligStartdato" : "2022-06-21",
                 "sluttdato" : "2022-06-29",
                 "startdato" : "2022-06-21"
            },
            "opprettet": "2022-06-29T16:24:02.608",
            "brukerRegistrertStartDato": "2022-06-21",
            "brukerRegistrertSluttDato": "2022-06-30",
            "systemRegistrertStartDato": null,
            "systemRegistrertSluttDato": null,
            "barnetillegg": [],
            "vedlegg": []
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

}
