package no.nav.tiltakspenger.vedtak.rivers

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import no.nav.tiltakspenger.vedtak.client.IVedtakClient
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ArenaTiltakMottattRiverTest {

    private val testRapid = TestRapid()

    private val vedtakClient = mockk<IVedtakClient>()

    init {
        ArenaTiltakMottattRiver(rapidsConnection = testRapid, vedtakClient = vedtakClient)
    }

    @AfterEach
    fun reset() {
        testRapid.reset()
    }

    @Test
    fun `Når en løsning for arenaTiltak mottas, så videresender vi data til tiltakspenger-vedtak`() {
        coEvery { vedtakClient.mottaTiltak(any(), "42") } returns Unit
        val arenaTiltakMottattHendelse =
            javaClass.getResource("/arenaTiltakMottattHendelse.json")?.readText(Charsets.UTF_8)!!
        testRapid.sendTestMessage(arenaTiltakMottattHendelse)
        coVerify { vedtakClient.mottaTiltak(any(), "42") }
    }

    @Test
    fun `Når en løsning for arenaTiltak mottas, så videresender vi data til tiltakspenger-vedtak som feiler`() {
        coEvery { vedtakClient.mottaTiltak(any(), "42") } throws RuntimeException()
        val arenaTiltakMottattHendelse =
            javaClass.getResource("/arenaTiltakMottattHendelse.json")?.readText(Charsets.UTF_8)!!
        assertThrows<RuntimeException> {
            testRapid.sendTestMessage(arenaTiltakMottattHendelse)
        }
        coVerify { vedtakClient.mottaTiltak(any(), "42") }
    }
}
