package no.nav.tiltakspenger.vedtak.rivers

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import mu.withLoggingContext
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.helse.rapids_rivers.asLocalDateTime
import no.nav.tiltakspenger.libs.ufore.UforeResponsDTO
import no.nav.tiltakspenger.vedtak.client.IVedtakClient

internal class UføreMottattRiver(
    private val vedtakClient: IVedtakClient,
    rapidsConnection: RapidsConnection,
) : River.PacketListener {

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandAllOrAny("@behov", listOf("uføre"))
                it.demandKey("@løsning")
                it.demandKey("@løsning.uføre")
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
            loggBehovVedInngang("uføre", packet)
            withLoggingContext(
                "id" to packet["@id"].asText(),
                "behovId" to packet["@behovId"].asText(),
            ) {
                val ident = packet["ident"].asText()
                val behovId = packet["@behovId"].asText()
                val innhentet = packet["@opprettet"].asLocalDateTime()
                val journalpostId = packet["journalpostId"].asText()
                val dto = packet["@løsning.uføre"].asObject(UforeResponsDTO::class.java)

                runBlocking(MDCContext()) {
                    vedtakClient.mottaUføre(
                        uføreDTO = UføreDTO(
                            ident = ident,
                            journalpostId = journalpostId,
                            uføre = dto,
                            innhentet = innhentet,
                        ),
                        behovId = behovId,
                    )
                }
                loggBehovVedUtgang("uføre", packet)
            }
        }.onFailure {
            loggBehovVedFeil("uføre", it, packet)
        }.getOrThrow()
    }
}
