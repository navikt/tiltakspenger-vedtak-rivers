package no.nav.tiltakspenger.vedtak

import mu.KotlinLogging
import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.tiltakspenger.vedtak.client.DokumentClient
import no.nav.tiltakspenger.vedtak.client.VedtakClient
import no.nav.tiltakspenger.vedtak.oauth.AzureTokenProvider
import no.nav.tiltakspenger.vedtak.rivers.søknad.SøknadMottattRiver
import no.nav.tiltakspenger.vedtak.rivers.vedtaksbrev.VedtaksBrevRiver

fun main() {
    System.setProperty("logback.configurationFile", Configuration.logbackConfigurationFile())

    val log = KotlinLogging.logger {}
    val securelog = KotlinLogging.logger("tjenestekall")
    log.info { "starting server" }
    Thread.setDefaultUncaughtExceptionHandler { _, e ->
        log.error { "Uncaught exception logget i securelog" }
        securelog.error(e) { e.message }
    }

    val dokumentTokenProvider =
        AzureTokenProvider(config = Configuration.oauthConfig(scope = Configuration.dokumentScope()))
    val vedtakTokenProvider =
        AzureTokenProvider(config = Configuration.oauthConfig(scope = Configuration.vedtakScope()))

    val vedtakClient = VedtakClient(
        getToken = vedtakTokenProvider::getToken,
    )
    val dokumentClient = DokumentClient(
        getToken = dokumentTokenProvider::getToken,
    )
    val rapidConfig = if (Configuration.applicationProfile() == Profile.LOCAL) {
        RapidApplication.RapidApplicationConfig.fromEnv(Configuration.rapidsAndRivers, LokalKafkaConfig())
    } else {
        RapidApplication.RapidApplicationConfig.fromEnv(Configuration.rapidsAndRivers)
    }
    val rapidsConnection: RapidsConnection = RapidApplication.Builder(rapidConfig).build()
    rapidsConnection.apply {
        SøknadMottattRiver(
            rapidsConnection = this,
            vedtakClient = vedtakClient,
        )
        VedtaksBrevRiver(
            rapidsConnection = this,
            dokumentClient = dokumentClient,
        )
        register(
            object : RapidsConnection.StatusListener {
                override fun onStartup(rapidsConnection: RapidsConnection) {
                    log.info { "Starting tiltakspenger-vedtak-rivers" }
                }

                override fun onShutdown(rapidsConnection: RapidsConnection) {
                    log.info { "Stopping tiltakspenger-vedtak-rivers" }
                    super.onShutdown(rapidsConnection)
                }
            },
        )
    }.start()
}
