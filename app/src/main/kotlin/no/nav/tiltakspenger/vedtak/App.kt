package no.nav.tiltakspenger.vedtak

import mu.KotlinLogging
import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.tiltakspenger.vedtak.rivers.ArenaTiltakMottattRiver
import no.nav.tiltakspenger.vedtak.rivers.ArenaYtelserMottattRiver
import no.nav.tiltakspenger.vedtak.rivers.PersonopplysningerMottattRiver
import no.nav.tiltakspenger.vedtak.rivers.SkjermingMottattRiver
import no.nav.tiltakspenger.vedtak.rivers.SøknadMottattRiver

fun main() {
    System.setProperty("logback.configurationFile", Configuration.logbackConfigurationFile())

    val log = KotlinLogging.logger {}
    val securelog = KotlinLogging.logger("tjenestekall")
    log.info { "starting server" }
    Thread.setDefaultUncaughtExceptionHandler { _, e ->
        log.error { "Uncaught exception logget i securelog" }
        securelog.error(e) { e.message }
    }
//
//    val azureADIssuer: OIDCIssuer = runBlocking(Dispatchers.IO) {
//        OIDCIssuer.discover(Config[Azure.openid_config_issuer]) {
//            when (Config.environment) {
//                Environment.LOCAL -> Stub.azure()
//                else -> CIO.create()
//            }
//        }
//    }
//    val hotsakApiConsumer = HotsakApiConsumer(
//        Config[Hotsak.api_base_url],
//        azureADIssuer.client(
//            OIDCClientMetadata(
//                clientId = Config[Azure.app_client_id],
//                clientSecret = Config[Azure.app_client_secret],
//                scope = Config[Hotsak.api_client_scope],
//            )
//        )
//    ) {
//        when (Config.environment) {
//            Environment.LOCAL -> Stub.hotsakApi()
//            else -> CIO.create()
//        }
//    }

    RapidApplication.Builder(
        RapidApplication.RapidApplicationConfig.fromEnv(Configuration.rapidsAndRivers)
    )
        .build()
        .also {
            SøknadMottattRiver(
                rapidsConnection = it
            )
            PersonopplysningerMottattRiver(
                rapidsConnection = it
            )
            SkjermingMottattRiver(
                rapidsConnection = it
            )
            ArenaTiltakMottattRiver(rapidsConnection = it)
            ArenaYtelserMottattRiver(rapidsConnection = it)
        }.start()
    log.info { "nå er vi i gang" }
}
