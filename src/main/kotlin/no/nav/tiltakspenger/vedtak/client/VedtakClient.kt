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
import no.nav.tiltakspenger.vedtak.rivers.events.DayHasBegunEvent
import no.nav.tiltakspenger.vedtak.rivers.foreldrepenger.ForeldrepengerDTO
import no.nav.tiltakspenger.vedtak.rivers.innsending.InnsendingUtdatert
import no.nav.tiltakspenger.vedtak.rivers.overgangsstønad.OvergangsstønadDTO
import no.nav.tiltakspenger.vedtak.rivers.personopplysninger.PersonopplysningerMottattDTO
import no.nav.tiltakspenger.vedtak.rivers.skjerming.SkjermingDTO
import no.nav.tiltakspenger.vedtak.rivers.søknad.SøknadDTO
import no.nav.tiltakspenger.vedtak.rivers.tiltak.ArenaTiltakMottattDTO
import no.nav.tiltakspenger.vedtak.rivers.tiltak.TiltakMottattDTO
import no.nav.tiltakspenger.vedtak.rivers.uføre.UføreDTO
import no.nav.tiltakspenger.vedtak.rivers.ytelser.ArenaYtelserMottattDTO

interface IVedtakClient {
    suspend fun mottaOvergangsstønad(overgangsstønadDTO: OvergangsstønadDTO, behovId: String)
    suspend fun mottaUføre(uføreDTO: UføreDTO, behovId: String)
    suspend fun mottaForeldrepenger(foreldrepengerDTO: ForeldrepengerDTO, behovId: String)
    suspend fun mottaSkjerming(skjermingDTO: SkjermingDTO, behovId: String)
    suspend fun mottaTiltak(tiltakMottattDTO: TiltakMottattDTO, behovId: String)
    suspend fun mottaTiltak(tiltakMottattDTO: ArenaTiltakMottattDTO, behovId: String)
    suspend fun mottaYtelser(arenaYtelserMottattDTO: ArenaYtelserMottattDTO, behovId: String)
    suspend fun mottaSøknad(søknadDTO: SøknadDTO, journalpostId: String)
    suspend fun mottaPersonopplysninger(personopplysningerMottattDTO: PersonopplysningerMottattDTO, behovId: String)
    suspend fun mottaUtdatert(utdatertDTO: InnsendingUtdatert)
    suspend fun mottaDayHasBegun(dayHasBegunEvent: DayHasBegunEvent)
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

    override suspend fun mottaOvergangsstønad(overgangsstønadDTO: OvergangsstønadDTO, behovId: String) {
        val httpResponse = httpClient.preparePost("${clientConfig.baseUrl}/rivers/overgangsstonad") {
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

    override suspend fun mottaDayHasBegun(dayHasBegunEvent: DayHasBegunEvent) {
        val httpResponse = httpClient.preparePost("${clientConfig.baseUrl}/rivers/passageoftime/dayhasbegun") {
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
        val httpResponse = httpClient.preparePost("${clientConfig.baseUrl}/rivers/innsendingutdatert") {
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
        val httpResponse = httpClient.preparePost("${clientConfig.baseUrl}/rivers/foreldrepenger") {
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
        val httpResponse = httpClient.preparePost("${clientConfig.baseUrl}/rivers/ufore") {
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
        val httpResponse = httpClient.preparePost("${clientConfig.baseUrl}/rivers/skjerming") {
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

    override suspend fun mottaTiltak(tiltakMottattDTO: TiltakMottattDTO, behovId: String) {
        val httpResponse = httpClient.preparePost("${clientConfig.baseUrl}/rivers/tiltak") {
            header(navCallIdHeader, behovId)
            bearerAuth(getToken())
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            setBody(tiltakMottattDTO)
        }.execute()
        when (httpResponse.status) {
            HttpStatusCode.OK -> return
            else -> throw RuntimeException("error (responseCode=${httpResponse.status.value}) from Vedtak")
        }
    }

    override suspend fun mottaTiltak(tiltakMottattDTO: ArenaTiltakMottattDTO, behovId: String) {
        val httpResponse = httpClient.preparePost("${clientConfig.baseUrl}/rivers/tiltak") {
            header(navCallIdHeader, behovId)
            bearerAuth(getToken())
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            setBody(tiltakMottattDTO)
        }.execute()
        when (httpResponse.status) {
            HttpStatusCode.OK -> return
            else -> throw RuntimeException("error (responseCode=${httpResponse.status.value}) from Vedtak")
        }
    }

    override suspend fun mottaYtelser(arenaYtelserMottattDTO: ArenaYtelserMottattDTO, behovId: String) {
        val httpResponse = httpClient.preparePost("${clientConfig.baseUrl}/rivers/ytelser") {
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
        val httpResponse = httpClient.preparePost("${clientConfig.baseUrl}/rivers/personopplysninger") {
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
}
