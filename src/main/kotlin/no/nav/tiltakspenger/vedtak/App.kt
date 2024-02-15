package no.nav.tiltakspenger.vedtak

import mu.KotlinLogging
import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.tiltakspenger.vedtak.client.DokumentClient
import no.nav.tiltakspenger.vedtak.client.MeldekortClient
import no.nav.tiltakspenger.vedtak.client.VedtakClient
import no.nav.tiltakspenger.vedtak.oauth.AzureTokenProvider
import no.nav.tiltakspenger.vedtak.rivers.events.DayHasBegunRiver
import no.nav.tiltakspenger.vedtak.rivers.foreldrepenger.ForeldrepengerMottattRiver
import no.nav.tiltakspenger.vedtak.rivers.innsending.InnsendingUtdatertRiver
import no.nav.tiltakspenger.vedtak.rivers.meldekort.MeldekortGrunnlagRiver
import no.nav.tiltakspenger.vedtak.rivers.meldekort.VedtaksBrevRiver
import no.nav.tiltakspenger.vedtak.rivers.overgangsstønad.OvergangsstønadMottattRiver
import no.nav.tiltakspenger.vedtak.rivers.personopplysninger.PersonopplysningerMottattRiver
import no.nav.tiltakspenger.vedtak.rivers.skjerming.SkjermingMottattRiver
import no.nav.tiltakspenger.vedtak.rivers.søknad.SøknadMottattRiver
import no.nav.tiltakspenger.vedtak.rivers.tiltak.ArenaTiltakMottattRiver
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

    val meldekortTokenProvider = AzureTokenProvider(config = Configuration.oauthConfig(scope = Configuration.meldekortScope()))
    val dokumentTokenProvider = AzureTokenProvider(config = Configuration.oauthConfig(scope = Configuration.dokumentScope()))
    val vedtakTokenProvider = AzureTokenProvider(config = Configuration.oauthConfig(scope = Configuration.vedtakScope()))

    val vedtakClient = VedtakClient(
        getToken = vedtakTokenProvider::getToken,
    )
    val meldekortClient = MeldekortClient(
        getToken = meldekortTokenProvider::getToken,
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
            meldekortClient = meldekortClient,
        )

        TiltakMottattRiver(
            rapidsConnection = this,
            vedtakClient = vedtakClient,
        )

        ArenaTiltakMottattRiver(
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

        MeldekortGrunnlagRiver(
            rapidsConnection = this,
            meldekortClient = meldekortClient,
        )

        VedtaksBrevRiver(
            rapidsConnection = this,
            dokumentClient = dokumentClient,
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
