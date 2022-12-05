package no.nav.tiltakspenger.vedtak.rivers

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.spyk
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import no.nav.tiltakspenger.vedtak.client.IVedtakClient
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

internal class ArenaTiltakMottattRiverTest {

    private val testRapid = TestRapid()

    private val vedtakClient = spyk<IVedtakClient>()

    init {
        ArenaTiltakMottattRiver(rapidsConnection = testRapid, vedtakClient = vedtakClient)
    }

    @AfterEach
    fun reset() {
        testRapid.reset()
    }

    @Test
    fun `Når en løsning for arenaTiltak mottas, så videresender vi data til tiltakspenger-vedtak`() {
        val arenaTiltakMottattHendelse =
            javaClass.getResource("/arenaTiltakMottattHendelse.json")?.readText(Charsets.UTF_8)!!
        testRapid.sendTestMessage(arenaTiltakMottattHendelse)
        coEvery { vedtakClient.mottaTiltak(any(), any()) } returns Unit
        coVerify { vedtakClient.mottaTiltak(any(), any()) }
    }
}
