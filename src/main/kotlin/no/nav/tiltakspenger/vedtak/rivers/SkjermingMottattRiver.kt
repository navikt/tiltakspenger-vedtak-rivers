package no.nav.tiltakspenger.vedtak.rivers

import mu.KotlinLogging
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.tiltakspenger.vedtak.client.IVedtakClient

private val LOG = KotlinLogging.logger {}
private val SECURELOG = KotlinLogging.logger("tjenestekall")

internal class SkjermingMottattRiver(
    private val vedtakClient: IVedtakClient,
    rapidsConnection: RapidsConnection
) : River.PacketListener {

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandAllOrAny("@behov", listOf("skjerming"))
                it.demandKey("@løsning")
                it.requireKey("ident")
                it.requireKey("journalpostId")
                it.requireKey("@opprettet")
                it.interestedIn("@løsning.skjerming")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
//        LOG.info("Received skjerming")
//        SECURELOG.info("Received skjerming for ident id: ${packet["ident"].asText()}")
//
//        val skjermingMottattHendelse = SkjermingMottattHendelse(
//            aktivitetslogg = Aktivitetslogg(),
//            journalpostId = packet["journalpostId"].asText(),
//            ident = packet["ident"].asText(),
//            skjerming = Skjerming(
//                ident = packet["ident"].asText(),
//                skjerming = packet["@løsning.skjerming"].asBoolean(),
//                innhentet = packet["@opprettet"].asLocalDateTime()
//            )
//        )
//
//        innsendingMediator.håndter(skjermingMottattHendelse)
//        søkerMediator.håndter(skjermingMottattHendelse)
    }
}
