package no.nav.tiltakspenger.vedtak.client

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import no.nav.tiltakspenger.libs.arena.tiltak.ArenaTiltaksaktivitetResponsDTO
import no.nav.tiltakspenger.vedtak.rivers.tiltak.ArenaTiltakMottattDTO
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
            respons = ArenaTiltaksaktivitetResponsDTO(
                tiltaksaktiviteter = emptyList(),
                feil = null,
            ),
            ident = "ident",
            journalpostId = "journalpostId",
            innhentet = LocalDateTime.now(),
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
            respons = ArenaTiltaksaktivitetResponsDTO(
                tiltaksaktiviteter = emptyList(),
                feil = null,
            ),
            ident = "ident",
            journalpostId = "journalpostId",
            innhentet = LocalDateTime.now(),
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
            respons = ArenaTiltaksaktivitetResponsDTO(
                tiltaksaktiviteter = emptyList(),
                feil = null,
            ),
            ident = "ident",
            journalpostId = "journalpostId",
            innhentet = LocalDateTime.now(),
        )

        assertThrows<RuntimeException> {
            vedtakClient.mottaTiltak(
                arenaTiltakMottattDTO = arenaDTO,
                behovId = "BehovId",
            )
        }
    }
}
