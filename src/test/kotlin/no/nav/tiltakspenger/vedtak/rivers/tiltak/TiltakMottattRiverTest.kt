package no.nav.tiltakspenger.vedtak.rivers.tiltak

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import no.nav.tiltakspenger.vedtak.client.IVedtakClient
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TiltakMottattRiverTest {

    private val testRapid = TestRapid()

    private val vedtakClient = mockk<IVedtakClient>()

    init {
        TiltakMottattRiver(
            rapidsConnection = testRapid,
            vedtakClient = vedtakClient,
        )
    }

    @AfterEach
    fun reset() {
        testRapid.reset()
    }

    @Test
    fun `Når en løsning for tiltak mottas, så videresender vi data til tiltakspenger-vedtak`() {
        coEvery { vedtakClient.mottaTiltak(any(), "42") } returns Unit
        val tiltakMottattHendelse =
            javaClass.getResource("/tiltakMottattHendelse.json")?.readText(Charsets.UTF_8)!!
        testRapid.sendTestMessage(tiltakMottattHendelse)
        coVerify { vedtakClient.mottaTiltak(any(), "42") }
    }

    @Test
    fun `Når en løsning for tiltak mottas, så videresender vi data til tiltakspenger-vedtak som feiler`() {
        coEvery { vedtakClient.mottaTiltak(any(), "42") } throws RuntimeException()
        val tiltakMottattHendelse =
            javaClass.getResource("/tiltakMottattHendelse.json")?.readText(Charsets.UTF_8)!!
        assertThrows<RuntimeException> {
            testRapid.sendTestMessage(tiltakMottattHendelse)
        }
        coVerify { vedtakClient.mottaTiltak(any(), "42") }
    }

    @Test
    fun `Når en løsning er på et annet behov i en multibehovsmelding så skal meldingen ignoreres`() {
        coEvery { vedtakClient.mottaTiltak(any(), "2d7b311c-f8a8-4b73-b256-de6f3d709cc7") } returns Unit
        testRapid.sendTestMessage(løsningPåEtAnnetBehovNårFlereBehovErISammeMelding)

        coVerify(exactly = 0) { vedtakClient.mottaTiltak(any(), "2d7b311c-f8a8-4b73-b256-de6f3d709cc7") }
    }

    private val løsningPåEtAnnetBehovNårFlereBehovErISammeMelding =
        """
            {"@event_name":"behov","@opprettet":"2023-01-27T11:10:58.856404701","@id":"0c2c7194-d674-4355-8bf5-fca9eeaabbba","@behovId":"2d7b311c-f8a8-4b73-b256-de6f3d709cc7","@behov":["personopplysninger","skjerming","arenaytelser","tiltak"],"journalpostId":"573803352","tilstandtype":"InnsendingFerdigstilt","ident":"01826799559","barn":[],"system_read_count":2,"system_participating_services":[{"id":"56549cc7-bf58-4954-838e-a86b5c6c33e0","time":"2023-01-27T11:10:55.108097453","service":"tiltakspenger-vedtak","instance":"tiltakspenger-vedtak-67cccd546b-l5hkd","image":"ghcr.io/navikt/tiltakspenger-vedtak:f980e9aa76efb91995f6636f7e73e6d597a68d06"},{"id":"56549cc7-bf58-4954-838e-a86b5c6c33e0","time":"2023-01-27T11:10:58.670817612","service":"tiltakspenger-skjerming","instance":"tiltakspenger-skjerming-dcd755f-xx44t","image":"ghcr.io/navikt/tiltakspenger-skjerming:2720edb825717e16710934e29caf6c9ef0036ff2"},{"id":"0c2c7194-d674-4355-8bf5-fca9eeaabbba","time":"2023-01-27T11:10:58.856404701","service":"tiltakspenger-skjerming","instance":"tiltakspenger-skjerming-dcd755f-xx44t","image":"ghcr.io/navikt/tiltakspenger-skjerming:2720edb825717e16710934e29caf6c9ef0036ff2"},{"id":"0c2c7194-d674-4355-8bf5-fca9eeaabbba","time":"2023-01-27T11:26:29.832397427","service":"tiltakspenger-vedtak-rivers","instance":"tiltakspenger-vedtak-rivers-5b7696966b-fqnqz","image":"ghcr.io/navikt/tiltakspenger-vedtak-rivers:3a24ce2b05557d44a5ad131a3ff5deb416ef353f"}],"@løsning":{"skjerming":{"skjermingForPersoner":{"søker":{"ident":"01826799559","skjerming":false},"barn":[]},"feil":null}},"@forårsaket_av":{"id":"56549cc7-bf58-4954-838e-a86b5c6c33e0","opprettet":"2023-01-27T11:10:55.10775169","event_name":"behov","behov":["personopplysninger","skjerming","arenaytelser","arenatiltak"]}}
        """.trimIndent()
}
