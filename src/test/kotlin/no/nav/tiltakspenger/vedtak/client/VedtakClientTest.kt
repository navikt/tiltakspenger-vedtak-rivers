package no.nav.tiltakspenger.vedtak.client

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import no.nav.tiltakspenger.libs.tiltak.TiltakResponsDTO
import no.nav.tiltakspenger.vedtak.rivers.tiltak.TiltakMottattDTO
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

internal class VedtakClientTest {

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    @Test
    fun `happy case`() = runTest {
        val vedtakClient = VedtakClient(
            getToken = { "token" },
            engine = MockEngine {
                respond(content = "")
            },
        )

        val tiltakDTO = TiltakMottattDTO(
            respons = TiltakResponsDTO(
                tiltak = emptyList(),
                feil = null,
            ),
            ident = "ident",
            journalpostId = "journalpostId",
            innhentet = LocalDateTime.now(),
        )

        assertDoesNotThrow {
            vedtakClient.mottaTiltak(
                tiltakMottattDTO = tiltakDTO,
                behovId = "BehovId",
            )
        }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    @Test
    fun `kaster exception ved feil fra vedtak`() = runTest {
        val vedtakClient = VedtakClient(
            getToken = { "token" },
            engine = MockEngine {
                respond(status = HttpStatusCode.BadRequest, content = "")
            },
        )

        val tiltakDTO = TiltakMottattDTO(
            respons = TiltakResponsDTO(
                tiltak = emptyList(),
                feil = null,
            ),
            ident = "ident",
            journalpostId = "journalpostId",
            innhentet = LocalDateTime.now(),
        )

        assertThrows<RuntimeException> {
            vedtakClient.mottaTiltak(
                tiltakMottattDTO = tiltakDTO,
                behovId = "BehovId",
            )
        }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    @Test
    fun `kaster exception ved autentiseringsfeil fra vedtak`() = runTest {
        val vedtakClient = VedtakClient(
            getToken = { "token" },
            engine = MockEngine {
                respond(status = HttpStatusCode.Unauthorized, content = "")
            },
        )

        val tiltakDTO = TiltakMottattDTO(
            respons = TiltakResponsDTO(
                tiltak = emptyList(),
                feil = null,
            ),
            ident = "ident",
            journalpostId = "journalpostId",
            innhentet = LocalDateTime.now(),
        )

        assertThrows<RuntimeException> {
            vedtakClient.mottaTiltak(
                tiltakMottattDTO = tiltakDTO,
                behovId = "BehovId",
            )
        }
    }
}
