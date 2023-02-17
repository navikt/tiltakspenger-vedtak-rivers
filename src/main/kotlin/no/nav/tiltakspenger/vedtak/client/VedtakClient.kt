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
import no.nav.tiltakspenger.vedtak.Configuration
import no.nav.tiltakspenger.vedtak.defaultHttpClient
import no.nav.tiltakspenger.vedtak.defaultObjectMapper
import no.nav.tiltakspenger.vedtak.rivers.ArenaTiltakMottattDTO
import no.nav.tiltakspenger.vedtak.rivers.ArenaYtelserMottattDTO
import no.nav.tiltakspenger.vedtak.rivers.DayHasBegunEvent
import no.nav.tiltakspenger.vedtak.rivers.ForeldrepengerDTO
import no.nav.tiltakspenger.vedtak.rivers.InnsendingUtdatert
import no.nav.tiltakspenger.vedtak.rivers.OvergangsstønadDTO
import no.nav.tiltakspenger.vedtak.rivers.PersonopplysningerMottattDTO
import no.nav.tiltakspenger.vedtak.rivers.SkjermingDTO
import no.nav.tiltakspenger.vedtak.rivers.SøknadDTO
import no.nav.tiltakspenger.vedtak.rivers.UføreDTO

interface IVedtakClient {
    suspend fun mottaOvergangsstønad(overgangsstønadDTO: OvergangsstønadDTO, behovId: String)
    suspend fun mottaUføre(uføreDTO: UføreDTO, behovId: String)
    suspend fun mottaForeldrepenger(foreldrepengerDTO: ForeldrepengerDTO, behovId: String)
    suspend fun mottaSkjerming(skjermingDTO: SkjermingDTO, behovId: String)
    suspend fun mottaTiltak(arenaTiltakMottattDTO: ArenaTiltakMottattDTO, behovId: String)
    suspend fun mottaYtelser(arenaYtelserMottattDTO: ArenaYtelserMottattDTO, behovId: String)
    suspend fun mottaSøknad(søknadDTO: SøknadDTO, journalpostId: String)
    suspend fun mottaPersonopplysninger(personopplysningerMottattDTO: PersonopplysningerMottattDTO, behovId: String)
    suspend fun mottaUtdatert(utdatertDTO: InnsendingUtdatert)
    suspend fun mottaDayHasBegun(dayHasBegunEvent: DayHasBegunEvent)
}

class VedtakClient(
    private val vedtakClientConfig: VedtakClientConfig = Configuration.vedtakClientConfig(),
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

    override suspend fun mottaOvergangsstønad(overgangsstønadDTO: OvergangsstønadDTO, behovId: String) {
        val httpResponse = httpClient.preparePost("${vedtakClientConfig.baseUrl}/rivers/overgangsstonad") {
            header(navCallIdHeader, behovId)
            bearerAuth(getToken())
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            setBody(overgangsstønadDTO)
        }.execute()
        when (httpResponse.status) {
            HttpStatusCode.OK -> return
            else -> throw RuntimeException("error (responseCode=${httpResponse.status.value}) from Vedtak")
        }
    }

    override suspend fun mottaSøknad(søknadDTO: SøknadDTO, journalpostId: String) {
        val httpResponse = httpClient.preparePost("${vedtakClientConfig.baseUrl}/rivers/soknad") {
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

    override suspend fun mottaDayHasBegun(dayHasBegunEvent: DayHasBegunEvent) {
        val httpResponse = httpClient.preparePost("${vedtakClientConfig.baseUrl}/rivers/passageoftime/dayhasbegun") {
            header(navCallIdHeader, dayHasBegunEvent.date)
            bearerAuth(getToken())
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            setBody(dayHasBegunEvent)
        }.execute()
        when (httpResponse.status) {
            HttpStatusCode.OK -> return
            else -> throw RuntimeException("error (responseCode=${httpResponse.status.value}) from Vedtak")
        }
    }

    override suspend fun mottaUtdatert(utdatertDTO: InnsendingUtdatert) {
        val httpResponse = httpClient.preparePost("${vedtakClientConfig.baseUrl}/rivers/innsendingutdatert") {
            header(navCallIdHeader, utdatertDTO.journalpostId)
            bearerAuth(getToken())
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            setBody(utdatertDTO)
        }.execute()
        when (httpResponse.status) {
            HttpStatusCode.OK -> return
            else -> throw RuntimeException("error (responseCode=${httpResponse.status.value}) from Vedtak")
        }
    }

    override suspend fun mottaForeldrepenger(foreldrepengerDTO: ForeldrepengerDTO, behovId: String) {
        val httpResponse = httpClient.preparePost("${vedtakClientConfig.baseUrl}/rivers/foreldrepenger") {
            header(navCallIdHeader, behovId)
            bearerAuth(getToken())
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            setBody(foreldrepengerDTO)
        }.execute()
        when (httpResponse.status) {
            HttpStatusCode.OK -> return
            else -> throw RuntimeException("error (responseCode=${httpResponse.status.value}) from Vedtak")
        }
    }

    override suspend fun mottaUføre(uføreDTO: UføreDTO, behovId: String) {
        val httpResponse = httpClient.preparePost("${vedtakClientConfig.baseUrl}/rivers/ufore") {
            header(navCallIdHeader, behovId)
            bearerAuth(getToken())
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            setBody(uføreDTO)
        }.execute()
        when (httpResponse.status) {
            HttpStatusCode.OK -> return
            else -> throw RuntimeException("error (responseCode=${httpResponse.status.value}) from Vedtak")
        }
    }

    override suspend fun mottaSkjerming(skjermingDTO: SkjermingDTO, behovId: String) {
        val httpResponse = httpClient.preparePost("${vedtakClientConfig.baseUrl}/rivers/skjerming") {
            header(navCallIdHeader, behovId)
            bearerAuth(getToken())
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            setBody(skjermingDTO)
        }.execute()
        when (httpResponse.status) {
            HttpStatusCode.OK -> return
            else -> throw RuntimeException("error (responseCode=${httpResponse.status.value}) from Vedtak")
        }
    }

    override suspend fun mottaTiltak(arenaTiltakMottattDTO: ArenaTiltakMottattDTO, behovId: String) {
        val httpResponse = httpClient.preparePost("${vedtakClientConfig.baseUrl}/rivers/tiltak") {
            header(navCallIdHeader, behovId)
            bearerAuth(getToken())
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            setBody(arenaTiltakMottattDTO)
        }.execute()
        when (httpResponse.status) {
            HttpStatusCode.OK -> return
            else -> throw RuntimeException("error (responseCode=${httpResponse.status.value}) from Vedtak")
        }
    }

    override suspend fun mottaYtelser(arenaYtelserMottattDTO: ArenaYtelserMottattDTO, behovId: String) {
        val httpResponse = httpClient.preparePost("${vedtakClientConfig.baseUrl}/rivers/ytelser") {
            header(navCallIdHeader, behovId)
            bearerAuth(getToken())
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            setBody(arenaYtelserMottattDTO)
        }.execute()
        when (httpResponse.status) {
            HttpStatusCode.OK -> return
            else -> throw RuntimeException("error (responseCode=${httpResponse.status.value}) from Vedtak")
        }
    }

    override suspend fun mottaPersonopplysninger(
        personopplysningerMottattDTO: PersonopplysningerMottattDTO,
        behovId: String,
    ) {
        val httpResponse = httpClient.preparePost("${vedtakClientConfig.baseUrl}/rivers/personopplysninger") {
            header(navCallIdHeader, behovId)
            bearerAuth(getToken())
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            setBody(personopplysningerMottattDTO)
        }.execute()
        when (httpResponse.status) {
            HttpStatusCode.OK -> return
            else -> throw RuntimeException("error (responseCode=${httpResponse.status.value}) from Vedtak")
        }
    }

    data class VedtakClientConfig(
        val baseUrl: String,
    )
}
