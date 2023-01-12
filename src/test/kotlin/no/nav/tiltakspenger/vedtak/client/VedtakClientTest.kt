package no.nav.tiltakspenger.vedtak.client

import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import no.nav.tiltakspenger.vedtak.rivers.ArenaTiltakMottattDTO
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

        val arenaDTO = ArenaTiltakMottattDTO(
            tiltak = null,
            ident = "ident",
            journalpostId = "journalpostId",
            innhentet = LocalDateTime.now(),
            feil = null,
        )

        assertDoesNotThrow {
            vedtakClient.mottaTiltak(
                arenaTiltakMottattDTO = arenaDTO,
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

        val arenaDTO = ArenaTiltakMottattDTO(
            tiltak = null,
            ident = "ident",
            journalpostId = "journalpostId",
            innhentet = LocalDateTime.now(),
            feil = null,
        )

        assertThrows<RuntimeException> {
            vedtakClient.mottaTiltak(
                arenaTiltakMottattDTO = arenaDTO,
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

        val arenaDTO = ArenaTiltakMottattDTO(
            tiltak = null,
            ident = "ident",
            journalpostId = "journalpostId",
            innhentet = LocalDateTime.now(),
            feil = null,
        )

        assertThrows<RuntimeException> {
            vedtakClient.mottaTiltak(
                arenaTiltakMottattDTO = arenaDTO,
                behovId = "BehovId",
            )
        }
    }
}
