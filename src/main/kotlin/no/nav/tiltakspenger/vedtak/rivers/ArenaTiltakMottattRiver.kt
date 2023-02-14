package no.nav.tiltakspenger.vedtak.rivers

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import mu.withLoggingContext
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.helse.rapids_rivers.asLocalDateTime
import no.nav.tiltakspenger.libs.arena.tiltak.ArenaTiltaksaktivitetResponsDTO
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
                it.demandKey("@løsning.arenatiltak")
                it.demandKey("@id")
                it.demandKey("@behovId")
                it.requireKey("ident")
                it.requireKey("journalpostId")
                it.requireKey("@opprettet")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        runCatching {
            loggBehovVedInngang("arenatiltak", packet)
            withLoggingContext(
                "id" to packet["@id"].asText(),
                "behovId" to packet["@behovId"].asText(),
            ) {
                val ident = packet["ident"].asText()
                val behovId = packet["@behovId"].asText()
                val tiltak =
                    if (packet["@løsning.arenatiltak"].asText() == "null") {
                        null
                    } else {
                        packet["@løsning.arenatiltak"]
                    }
                val journalpostId = packet["journalpostId"].asText()
                val innhentet = packet["@opprettet"].asLocalDateTime()

                runBlocking(MDCContext()) {
                    vedtakClient.mottaTiltak(
                        arenaTiltakMottattDTO = ArenaTiltakMottattDTO(
                            respons = tiltak.asObject(ArenaTiltaksaktivitetResponsDTO::class.java),
                            ident = ident,
                            journalpostId = journalpostId,
                            innhentet = innhentet,
                        ),
                        behovId = behovId,
                    )
                }
                loggBehovVedUtgang("arenatiltak", packet)
            }
        }.onFailure {
            loggBehovVedFeil("arenatiltak", it, packet)
        }.getOrThrow()
    }
}
