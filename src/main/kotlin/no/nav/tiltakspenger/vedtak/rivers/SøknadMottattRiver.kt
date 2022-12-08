package no.nav.tiltakspenger.vedtak.rivers

import mu.KotlinLogging
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.tiltakspenger.vedtak.client.IVedtakClient

private val LOG = KotlinLogging.logger {}
private val SECURELOG = KotlinLogging.logger("tjenestekall")

internal class SøknadMottattRiver(
    private val vedtakClient: IVedtakClient,
    rapidsConnection: RapidsConnection
) : River.PacketListener {

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandValue("@event_name", "søknad_mottatt")
                it.requireKey("@id")
                it.requireKey("@opprettet")
                it.requireKey("søknad")
                it.requireKey("søknad.journalpostId")
                it.requireKey("søknad.ident")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        LOG.info("Received søknad")
        LOG.debug { vedtakClient }
        SECURELOG.info("Received søknad: ${packet.toJson()}")
//
//        //Metrics.mottakskanalInc(packet["mottaksKanal"].asText())
//
//        val søknad = mapSøknad(
//            dto = packet["søknad"].asObject(SøknadDTO::class.java),
//            innhentet = packet["@opprettet"].asLocalDateTime()
//        )
//        val søknadMottattHendelse = SøknadMottattHendelse(
//            aktivitetslogg = Aktivitetslogg(),
//            journalpostId = packet["søknad.journalpostId"].asText(),
//            søknad = søknad
//        )
//
//        innsendingMediator.håndter(søknadMottattHendelse)
//
//        val identMottattHendelse = IdentMottattHendelse(
//            aktivitetslogg = Aktivitetslogg(),
//            ident = packet["søknad.ident"].asText(),
//        )
//        søkerMediator.håndter(identMottattHendelse)
    }

}
