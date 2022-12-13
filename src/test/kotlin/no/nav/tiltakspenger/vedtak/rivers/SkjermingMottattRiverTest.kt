package no.nav.tiltakspenger.vedtak.rivers

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import no.nav.tiltakspenger.vedtak.client.IVedtakClient
import no.nav.tiltakspenger.vedtak.client.VedtakClient
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class SkjermingMottattRiverTest {

    private val vedtakClient = mockk<IVedtakClient>()
    private val testRapid = TestRapid()


    init {
        SkjermingMottattRiver(
            vedtakClient = vedtakClient,
            rapidsConnection = testRapid,
        )
    }

    @AfterEach
    fun reset() {
        testRapid.reset()
    }

    @Test
    fun `Når en løsning for skjerming mottas, så videresender vi data til tiltakspenger-vedtak`() {
        coEvery { vedtakClient.mottaSkjerming(any(), any()) } returns Unit
        testRapid.sendTestMessage(løsning)
        coVerify { vedtakClient.mottaSkjerming(any(), "behovId") }
    }

    private val løsning = """
            {
              "@behov": [
                "skjerming"
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
                "skjerming": false
              }
            }
        """
}
