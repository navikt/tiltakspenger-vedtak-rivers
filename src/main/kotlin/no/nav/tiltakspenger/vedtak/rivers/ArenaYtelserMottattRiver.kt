package no.nav.tiltakspenger.vedtak.rivers

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import mu.withLoggingContext
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.helse.rapids_rivers.asLocalDateTime
import no.nav.tiltakspenger.libs.arena.ytelse.ArenaYtelseResponsDTO
import no.nav.tiltakspenger.vedtak.client.IVedtakClient

internal class ArenaYtelserMottattRiver(
    private val vedtakClient: IVedtakClient,
    rapidsConnection: RapidsConnection,
) : River.PacketListener {

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandAllOrAny("@behov", listOf("arenaytelser"))
                it.demandKey("@løsning")
                it.demandKey("@løsning.arenaytelser")
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
            loggBehovVedInngang("arenaytelser", packet)
            withLoggingContext(
                "id" to packet["@id"].asText(),
                "behovId" to packet["@behovId"].asText(),
            ) {
                val ident = packet["ident"].asText()
                val behovId = packet["@behovId"].asText()
                val ytelser =
                    if (packet["@løsning.arenaytelser"].asText() == "null")
                        null
                    else packet["@løsning.arenaytelser"]
                val journalpostId = packet["journalpostId"].asText()
                val innhentet = packet["@opprettet"].asLocalDateTime()

                runBlocking(MDCContext()) {
                    vedtakClient.mottaYtelser(
                        arenaYtelserMottattDTO = ArenaYtelserMottattDTO(
                            respons = ytelser.asObject(ArenaYtelseResponsDTO::class.java),
                            ident = ident,
                            journalpostId = journalpostId,
                            innhentet = innhentet,
                        ),
                        behovId = behovId,
                    )
                }
                loggBehovVedUtgang("arenaytelser", packet)
            }
        }.onFailure {
            loggBehovVedFeil("arenaytelser", it, packet)
        }.getOrThrow()
    }
}
