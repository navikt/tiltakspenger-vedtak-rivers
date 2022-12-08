package no.nav.tiltakspenger.vedtak

import io.ktor.client.engine.cio.*
import mu.KotlinLogging
import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.tiltakspenger.vedtak.client.VedtakClient
import no.nav.tiltakspenger.vedtak.oauth.AzureTokenProvider
import no.nav.tiltakspenger.vedtak.rivers.ArenaTiltakMottattRiver

fun main() {
    System.setProperty("logback.configurationFile", Configuration.logbackConfigurationFile())

    val log = KotlinLogging.logger {}
    val securelog = KotlinLogging.logger("tjenestekall")
    log.info { "starting server" }
    Thread.setDefaultUncaughtExceptionHandler { _, e ->
        log.error { "Uncaught exception logget i securelog" }
        securelog.error(e) { e.message }
    }

    val tokenProvider = AzureTokenProvider()

    val vedtakClient = VedtakClient(
        getToken = tokenProvider::getToken,
    )

    RapidApplication.create(Configuration.rapidsAndRivers).apply {

        ArenaTiltakMottattRiver(
            rapidsConnection = this,
            vedtakClient = vedtakClient,
        )

        register(object : RapidsConnection.StatusListener {
            override fun onStartup(rapidsConnection: RapidsConnection) {
                log.info { "Starting tiltakspenger-vedtak-rivers" }
            }

            override fun onShutdown(rapidsConnection: RapidsConnection) {
                log.info { "Stopping tiltakspenger-vedtak-rivers" }
                super.onShutdown(rapidsConnection)
            }
        })
    }.start()
}
