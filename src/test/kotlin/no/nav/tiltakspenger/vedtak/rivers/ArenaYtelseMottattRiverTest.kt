package no.nav.tiltakspenger.vedtak.rivers

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import no.nav.tiltakspenger.vedtak.client.IVedtakClient
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

internal class ArenaYtelseMottattRiverTest {

    private val testRapid = TestRapid()

    private val vedtakClient = mockk<IVedtakClient>()

    init {
        ArenaYtelserMottattRiver(rapidsConnection = testRapid, vedtakClient = vedtakClient)
    }

    @AfterEach
    fun reset() {
        testRapid.reset()
    }

    @Test
    fun `Når en løsning for arenaYtelser mottas, så videresender vi data til tiltakspenger-vedtak`() {
        coEvery { vedtakClient.mottaYtelser(any(), any()) } returns Unit
        val arenaYtelserMottattHendelse =
            javaClass.getResource("/arenaYtelserMottattHendelse.json")?.readText(Charsets.UTF_8)!!
        testRapid.sendTestMessage(arenaYtelserMottattHendelse)
        coVerify { vedtakClient.mottaYtelser(any(), any()) }
    }
}
