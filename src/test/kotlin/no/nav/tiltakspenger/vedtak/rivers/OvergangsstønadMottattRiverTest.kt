package no.nav.tiltakspenger.vedtak.rivers

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import no.nav.tiltakspenger.vedtak.client.IVedtakClient
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

internal class OvergangsstønadMottattRiverTest {

    private val vedtakClient = mockk<IVedtakClient>()
    private val testRapid = TestRapid()

    init {
        OvergangsstønadMottattRiver(
            vedtakClient = vedtakClient,
            rapidsConnection = testRapid,
        )
    }

    @AfterEach
    fun reset() {
        testRapid.reset()
    }

    @Test
    fun `Når en løsning for overgangsstønad mottas, så videresender vi data til tiltakspenger-vedtak`() {
        coEvery { vedtakClient.mottaOvergangsstønad(any(), any()) } returns Unit
        testRapid.sendTestMessage(løsning)
        coVerify { vedtakClient.mottaOvergangsstønad(any(), "behovId") }
    }

    @Language("JSON")
    private val løsning = """{
        "@behov": [
          "overgangsstønad"
        ],
        "@id": "test",
        "@behovId": "behovId",
        "journalpostId": "test",
        "ident": "test",
        "@opprettet": "2025-01-01T00:00:00",
        "@løsning": {
          "overgangsstønad": {
              "perioder": [
                {"fomDato":"2025-01-01","tomDato":"2025-01-10","datakilde":"kilde"}
              ],
              "status":"test",
              "melding":"test",
              "frontendFeilmelding":"test",
              "stacktrace":"test"
          }
        }
      }"""
}
