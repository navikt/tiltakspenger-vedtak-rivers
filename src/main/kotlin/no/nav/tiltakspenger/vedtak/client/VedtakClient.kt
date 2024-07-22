package no.nav.tiltakspenger.vedtak.client

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.header
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import no.nav.tiltakspenger.vedtak.ClientConfig
import no.nav.tiltakspenger.vedtak.Configuration
import no.nav.tiltakspenger.vedtak.Configuration.vedtakBaseUrl
import no.nav.tiltakspenger.vedtak.defaultHttpClient
import no.nav.tiltakspenger.vedtak.defaultObjectMapper
import no.nav.tiltakspenger.vedtak.rivers.søknad.SøknadDTO

interface IVedtakClient {
    suspend fun mottaSøknad(søknadDTO: SøknadDTO, journalpostId: String)
}

class VedtakClient(
    private val clientConfig: ClientConfig = Configuration.clientConfig(baseUrl = vedtakBaseUrl()),
    private val objectMapper: ObjectMapper = defaultObjectMapper(),
    private val getToken: suspend () -> String,
    engine: HttpClientEngine? = null,
    private val httpClient: HttpClient = defaultHttpClient(
        objectMapper = objectMapper,
        engine = engine,
    ) {},
) : IVedtakClient {
    companion object {
        const val navCallIdHeader = "Nav-Call-Id"
    }

    override suspend fun mottaSøknad(søknadDTO: SøknadDTO, journalpostId: String) {
        val httpResponse = httpClient.preparePost("${clientConfig.baseUrl}/rivers/soknad") {
            header(navCallIdHeader, journalpostId)
            bearerAuth(getToken())
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            setBody(søknadDTO)
        }.execute()
        when (httpResponse.status) {
            HttpStatusCode.OK -> return
            else -> throw RuntimeException("error (responseCode=${httpResponse.status.value}) from Vedtak")
        }
    }
}
