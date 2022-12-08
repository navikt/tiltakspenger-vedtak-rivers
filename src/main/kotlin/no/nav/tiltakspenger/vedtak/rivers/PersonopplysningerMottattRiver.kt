package no.nav.tiltakspenger.vedtak.rivers

import mu.KotlinLogging
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.tiltakspenger.vedtak.client.IVedtakClient

private val LOG = KotlinLogging.logger {}
private val SECURELOG = KotlinLogging.logger("tjenestekall")

internal class PersonopplysningerMottattRiver(
    private val vedtakClient: IVedtakClient,
    rapidsConnection: RapidsConnection,
) : River.PacketListener {

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandAllOrAny("@behov", listOf("personopplysninger"))
                it.demandKey("@løsning")
                it.demandKey("@id")
                it.demandKey("@behovId")
                it.requireKey("ident")
                it.requireKey("journalpostId")
                it.requireKey("@opprettet")
                it.interestedIn("@løsning.personopplysninger.person")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        LOG.info("Received personopplysninger")
        LOG.debug { vedtakClient }
        SECURELOG.info("Received personopplysninger for ident id: ${packet["ident"].asText()}")
//
//        //Metrics.mottakskanalInc(packet["mottaksKanal"].asText())
//
//        val personopplysningerMottattHendelse = PersonopplysningerMottattHendelse(
//            aktivitetslogg = Aktivitetslogg(),
//            journalpostId = packet["journalpostId"].asText(),
//            ident = packet["ident"].asText(),
//            personopplysninger = mapPersonopplysninger(
//                dto = packet["@løsning.personopplysninger.person"].asObject(PersonopplysningerDTO::class.java),
//                innhentet = packet["@opprettet"].asLocalDateTime(),
//                ident = packet["ident"].asText(),
//            ),
//        )
//
//        innsendingMediator.håndter(personopplysningerMottattHendelse)
//        søkerMediator.håndter(personopplysningerMottattHendelse)
    }
}
