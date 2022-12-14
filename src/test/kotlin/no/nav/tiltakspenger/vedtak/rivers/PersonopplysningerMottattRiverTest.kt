package no.nav.tiltakspenger.vedtak.rivers

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import no.nav.tiltakspenger.vedtak.client.IVedtakClient
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class PersonopplysningerMottattRiverTest {
    private val testRapid = TestRapid()

    private val vedtakClient = mockk<IVedtakClient>()

    init {
        PersonopplysningerMottattRiver(rapidsConnection = testRapid, vedtakClient = vedtakClient)
    }

    @AfterEach
    fun reset() {
        testRapid.reset()
    }

    @Test
    fun `Når en løsning for personopplysninger mottas, så videresender vi data til tiltakspenger-vedtak`() {
        coEvery { vedtakClient.mottaPersonopplysninger(any(), any()) } returns Unit
        val personopplysningerMottattHendelse =
            javaClass.getResource("/personopplysningerMottattHendelse.json")?.readText(Charsets.UTF_8)!!
        testRapid.sendTestMessage(personopplysningerMottattHendelse)
        coVerify { vedtakClient.mottaPersonopplysninger(any(), any()) }
    }

    @Test
    fun `Når en løsning for personopplysninger mottas, men kallet mot tiltakspenger-vedtak feiler, kaster vi en RuntimeException`() {
        coEvery { vedtakClient.mottaPersonopplysninger(any(), any()) } throws RuntimeException()
        val personopplysningerMottattHendelse =
            javaClass.getResource("/personopplysningerMottattHendelse.json")?.readText(Charsets.UTF_8)!!
        assertThrows<RuntimeException> {
            testRapid.sendTestMessage(personopplysningerMottattHendelse)
        }
    }
}
