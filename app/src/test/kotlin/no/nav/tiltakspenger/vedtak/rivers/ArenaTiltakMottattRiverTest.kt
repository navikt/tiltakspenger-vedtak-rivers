package no.nav.tiltakspenger.vedtak.rivers

import io.mockk.coVerify
import io.mockk.mockk
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import no.nav.tiltakspenger.vedtak.client.VedtakClient
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

internal class ArenaTiltakMottattRiverTest {

    private companion object {
        const val IDENT = "04927799109"
        const val JOURNALPOSTID = "foobar2"
    }

    private val testRapid = TestRapid()

    private val vedtakClient = mockk<VedtakClient>()

    init {
        ArenaTiltakMottattRiver(rapidsConnection = testRapid, vedtakClient = vedtakClient)
    }

    @AfterEach
    fun reset() {
        testRapid.reset()
    }

    @Test
    fun `Når ArenaTiltakMottattHendelse oppstår, så videresender vi data til tiltakspenger-vedtak`() {
        val arenaTiltakMottattHendelse =
            javaClass.getResource("/arenaTiltakMottattHendelse.json")?.readText(Charsets.UTF_8)!!
        testRapid.sendTestMessage(arenaTiltakMottattHendelse)
        coVerify { vedtakClient.mottaTiltak(any()) }
    }
}
