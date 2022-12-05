package no.nav.tiltakspenger.vedtak.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.isSuccess
import kotlinx.coroutines.runBlocking
import no.nav.tiltakspenger.vedtak.rivers.ArenaTiltakMottattDTO

class VedtakClient(
    private val baseUrl: String,
    azureADClient: OIDCClient,
    block: () -> HttpClientEngine,
) {
    private val client: HttpClient = HttpClient(block()) {
        expectSuccess = true
        json()
        install(Auth) {
            oidc(azureADClient)
        }
        install(HttpRequestRetry) {
            maxRetries = 5
            retryIf { _, response ->
                !response.status.isSuccess()
            }
            delayMillis { retry ->
                retry * 3000L
            }
        }
        defaultRequest {
            json()
        }
    }

    fun mottaTiltak(
        arenaTiltakMottattDTO: ArenaTiltakMottattDTO
    ) = runBlocking {
        client.post("$baseUrl/sak") {
            setBody(arenaTiltakMottattDTO)
        }
    }
}