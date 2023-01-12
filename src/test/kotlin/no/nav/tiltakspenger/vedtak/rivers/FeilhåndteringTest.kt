package no.nav.tiltakspenger.vedtak.rivers

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class FeilhåndteringTest {

    @Test
    fun testRunCatching() {
        assertThrows<IllegalArgumentException> {
            runCatching {
                throw IllegalArgumentException("Feil")
            }.onFailure {
                // Do nothing, simulate logging of error
            }.getOrThrow()
        }
    }
}
