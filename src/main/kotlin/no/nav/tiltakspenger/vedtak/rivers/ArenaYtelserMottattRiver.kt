package no.nav.tiltakspenger.vedtak.rivers

import mu.KotlinLogging
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.tiltakspenger.vedtak.client.IVedtakClient

private val LOG = KotlinLogging.logger {}
private val SECURELOG = KotlinLogging.logger("tjenestekall")

internal class ArenaYtelserMottattRiver(
    private val vedtakClient: IVedtakClient,
    rapidsConnection: RapidsConnection,
) : River.PacketListener {

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandAllOrAny("@behov", listOf("arenaytelser"))
                it.demandKey("@løsning")
                it.demandKey("@id")
                it.demandKey("@behovId")
                it.requireKey("ident")
                it.requireKey("journalpostId")
                it.requireKey("@opprettet")
                it.interestedIn("@løsning.arenaytelser")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        LOG.info("Received arenaytelser")
        LOG.debug { vedtakClient }
        SECURELOG.info("Received arenaytelser for ident id: ${packet["ident"].asText()}")
//
//        //Metrics.mottakskanalInc(packet["mottaksKanal"].asText())
//
//        val ytelserMottattHendelse = YtelserMottattHendelse(
//            aktivitetslogg = Aktivitetslogg(),
//            journalpostId = packet["journalpostId"].asText(),
//            ytelseSak = mapYtelser(
//                ytelseSakDTO = packet["@løsning.arenaytelser"].asList(),
//                tidsstempelHosOss = packet["@opprettet"].asLocalDateTime(),
//            )
//        )
//
//        innsendingMediator.håndter(ytelserMottattHendelse)
    }
}
