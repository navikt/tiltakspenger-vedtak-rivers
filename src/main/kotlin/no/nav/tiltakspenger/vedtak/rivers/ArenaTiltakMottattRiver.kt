package no.nav.tiltakspenger.vedtak.rivers

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import mu.withLoggingContext
import no.nav.helse.rapids_rivers.*
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
                it.demandKey("@id")
                it.demandKey("@behovId")
                it.requireKey("ident")
                it.requireKey("journalpostId")
                it.requireKey("@opprettet")
                it.interestedIn("@løsning.arenatiltak")
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
                    if (packet["@løsning.arenatiltak"].asText() == "null")
                        null
                    else packet["@løsning.arenatiltak"]
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
