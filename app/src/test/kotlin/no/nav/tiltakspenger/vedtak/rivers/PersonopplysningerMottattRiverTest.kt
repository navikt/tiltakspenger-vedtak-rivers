package no.nav.tiltakspenger.vedtak.rivers

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.spyk
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import no.nav.tiltakspenger.vedtak.client.VedtakClient
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled
internal class PersonopplysningerMottattRiverTest {
    private val testRapid = TestRapid()

    private val vedtakClient = spyk<VedtakClient>()

    init {
        PersonopplysningerMottattRiver(rapidsConnection = testRapid, vedtakClient = vedtakClient)
    }

    @AfterEach
    fun reset() {
        testRapid.reset()
    }

    @Test
    fun `Når en løsning for personopplysninger mottas, så videresender vi data til tiltakspenger-vedtak`() {
        val arenaTiltakMottattHendelse =
            javaClass.getResource("/personopplysningerMottattHendelse.json")?.readText(Charsets.UTF_8)!!
        testRapid.sendTestMessage(arenaTiltakMottattHendelse)
        coEvery { vedtakClient.mottaTiltak(any(), any()) } returns Unit
        coVerify { vedtakClient.mottaTiltak(any(), any()) }
    }
}
