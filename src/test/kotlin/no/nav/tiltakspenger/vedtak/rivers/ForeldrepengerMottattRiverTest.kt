package no.nav.tiltakspenger.vedtak.rivers

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import no.nav.tiltakspenger.vedtak.client.IVedtakClient
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

internal class ForeldrepengerMottattRiverTest {

    private val vedtakClient = mockk<IVedtakClient>()
    private val testRapid = TestRapid()

    init {
        ForeldrepengerMottattRiver(
            vedtakClient = vedtakClient,
            rapidsConnection = testRapid,
        )
    }

    @AfterEach
    fun reset() {
        testRapid.reset()
    }

    @Test
    fun `Når en løsning for foreldrepenger mottas, så videresender vi data til tiltakspenger-vedtak`() {
        coEvery { vedtakClient.mottaForeldrepenger(any(), any()) } returns Unit
        testRapid.sendTestMessage(løsning)
        coVerify { vedtakClient.mottaForeldrepenger(any(), "behovId") }
    }

    private val løsning = """
            {
              "@behov": [
                "fpytelser"
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
                "fpytelser": {
                  "ytelser": [
                    {
                      "version": "v1",
                      "aktør": "aktørId",
                      "vedtattTidspunkt": "2022-01-01T12:00:00",
                      "ytelse": "PLEIEPENGER_SYKT_BARN",
                      "saksnummer": "sakNr",
                      "vedtakReferanse": "Ref",
                      "ytelseStatus": "LØPENDE",
                      "kildesystem": "FPSAK",
                      "periode": {
                        "fom": "2022-01-01",
                        "tom": "2022-01-31"
                      },
                      "tilleggsopplysninger": "Tillegg",
                      "anvist": [
                          {
                            "periode": {
                              "fom": "2022-01-01",
                              "tom": "2022-01-31"
                            },
                            "beløp": 100.0,
                            "dagsats": 50.0,
                            "utbetalingsgrad": 10.0
                          }
                        ]
                    }
                  ],
                  "feil": null
                }
              }
            }
        """
}
