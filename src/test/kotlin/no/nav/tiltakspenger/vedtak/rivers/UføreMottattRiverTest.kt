package no.nav.tiltakspenger.vedtak.rivers

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import no.nav.tiltakspenger.vedtak.client.IVedtakClient
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

internal class UføreMottattRiverTest {

    private val vedtakClient = mockk<IVedtakClient>()
    private val testRapid = TestRapid()

    init {
        UføreMottattRiver(
            vedtakClient = vedtakClient,
            rapidsConnection = testRapid,
        )
    }

    @AfterEach
    fun reset() {
        testRapid.reset()
    }

    @Test
    fun `Når en løsning for uføre mottas, så videresender vi data til tiltakspenger-vedtak`() {
        coEvery { vedtakClient.mottaUføre(any(), any()) } returns Unit
        testRapid.sendTestMessage(løsning)
        coVerify { vedtakClient.mottaUføre(any(), "behovId") }
    }

    private val løsning = """
            {
              "@behov": [
                "uføre"
              ],
              "@id": "test",
              "@behovId": "behovId",
              "journalpostId": "wolla",
              "ident": "05906398291",
              "fom": "2019-10-01",
              "tom": "2022-06-01",
              "@opprettet": "2022-08-19T12:28:01.422516717",
              "system_read_count": 0,
              "system_participating_services": [
                {
                  "id": "test",
                  "time": "2022-08-19T12:28:01.422516717",
                  "service": "tiltakspenger-skjerming",
                  "instance": "tiltakspenger-skjerming-69f669bc95-plxb6",
                  "image": "ghcr.io/navikt/tiltakspenger-skjerming:128cdcc92ea50224bbccdc4c565e3f408e093213"
                }
              ],
              "@løsning": {
                "uføre": {
                  "uføregrad":
                    {
                      "harUforegrad": true,
                      "datoUfor": "-999999999-01-01",
                      "virkDato": "1970-01-01"
                    },
                  "feil": null
                }
              }
            }
        """
}
