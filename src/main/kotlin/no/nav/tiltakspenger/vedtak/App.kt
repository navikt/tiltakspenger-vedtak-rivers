package no.nav.tiltakspenger.vedtak

import mu.KotlinLogging
import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.tiltakspenger.vedtak.client.VedtakClient
import no.nav.tiltakspenger.vedtak.oauth.AzureTokenProvider
import no.nav.tiltakspenger.vedtak.rivers.events.DayHasBegunRiver
import no.nav.tiltakspenger.vedtak.rivers.foreldrepenger.ForeldrepengerMottattRiver
import no.nav.tiltakspenger.vedtak.rivers.innsending.InnsendingUtdatertRiver
import no.nav.tiltakspenger.vedtak.rivers.overgangsstønad.OvergangsstønadMottattRiver
import no.nav.tiltakspenger.vedtak.rivers.personopplysninger.PersonopplysningerMottattRiver
import no.nav.tiltakspenger.vedtak.rivers.skjerming.SkjermingMottattRiver
import no.nav.tiltakspenger.vedtak.rivers.søknad.SøknadMottattRiver
import no.nav.tiltakspenger.vedtak.rivers.tiltak.TiltakMottattRiver
import no.nav.tiltakspenger.vedtak.rivers.uføre.UføreMottattRiver
import no.nav.tiltakspenger.vedtak.rivers.ytelser.ArenaYtelserMottattRiver

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
        OvergangsstønadMottattRiver(
            rapidsConnection = this,
            vedtakClient = vedtakClient,
        )

        SøknadMottattRiver(
            rapidsConnection = this,
            vedtakClient = vedtakClient,
        )

        DayHasBegunRiver(
            rapidsConnection = this,
            vedtakClient = vedtakClient,
        )

        TiltakMottattRiver(
            rapidsConnection = this,
            vedtakClient = vedtakClient,
        )

        ArenaYtelserMottattRiver(
            rapidsConnection = this,
            vedtakClient = vedtakClient,
        )

        PersonopplysningerMottattRiver(
            rapidsConnection = this,
            vedtakClient = vedtakClient,
        )

        ForeldrepengerMottattRiver(
            rapidsConnection = this,
            vedtakClient = vedtakClient,
        )

        UføreMottattRiver(
            rapidsConnection = this,
            vedtakClient = vedtakClient,
        )

        SkjermingMottattRiver(
            rapidsConnection = this,
            vedtakClient = vedtakClient,
        )

        InnsendingUtdatertRiver(
            rapidsConnection = this,
            vedtakClient = vedtakClient,
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
