package no.nav.tiltakspenger.vedtak.rivers

import asList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import loggVedFeil
import loggVedInngang
import loggVedUtgang
import mu.withLoggingContext
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.helse.rapids_rivers.asLocalDateTime
import no.nav.tiltakspenger.vedtak.client.IVedtakClient


internal class ArenaTiltakMottattRiver(
    private val vedtakClient: IVedtakClient,
    rapidsConnection: RapidsConnection,
) : River.PacketListener {

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandAllOrAny("@behov", listOf("arenatiltak"))
                it.demandKey("@løsning")
                it.demandKey("@id")
                it.demandKey("@behovId")
                it.requireKey("ident")
                it.requireKey("journalpostId")
                it.requireKey("@opprettet")
                it.interestedIn("@løsning.arenatiltak.tiltaksaktiviteter")
                it.interestedIn("@løsning.arenatiltak.feil")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        runCatching {
            loggVedInngang("arenatiltak", packet)
            withLoggingContext(
                "id" to packet["@id"].asText(),
                "behovId" to packet["@behovId"].asText()
            ) {
                val ident = packet["ident"].asText()
                val behovId = packet["@behovId"].asText()
                val tiltak =
                    if (packet["@løsning.arenatiltak.tiltaksaktiviteter"].asText() == "null")
                        null
                    else packet["@løsning.arenatiltak.tiltaksaktiviteter"]
                val journalpostId = packet["journalpostId"].asText()
                val innhentet = packet["@opprettet"].asLocalDateTime()
                val feil = packet["@løsning.arenatiltak.feil"].asText(null)

                runBlocking(MDCContext()) {
                    vedtakClient.mottaTiltak(
                        arenaTiltakMottattDTO = ArenaTiltakMottattDTO(
                            tiltak = tiltak.asList(),
                            ident = ident,
                            journalpostId = journalpostId,
                            innhentet = innhentet,
                            feil = feil
                        ),
                        behovId = behovId
                    )
                }
                loggVedUtgang("arenatiltak", packet)
            }
        }.onFailure {
            loggVedFeil("arenatiltak", it, packet)
        }.getOrThrow()
    }


}
