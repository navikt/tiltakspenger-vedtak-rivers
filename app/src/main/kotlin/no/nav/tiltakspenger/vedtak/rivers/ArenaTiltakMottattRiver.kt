package no.nav.tiltakspenger.vedtak.rivers

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.type.CollectionType
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import mu.KotlinLogging
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.helse.rapids_rivers.asLocalDateTime
import no.nav.tiltakspenger.vedtak.client.VedtakClient

private val LOG = KotlinLogging.logger {}
private val SECURELOG = KotlinLogging.logger("tjenestekall")

internal class ArenaTiltakMottattRiver(
    private val vedtakClient: VedtakClient,
    rapidsConnection: RapidsConnection,
) : River.PacketListener {

    private companion object {
        private val objectMapper = jacksonObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandAllOrAny("@behov", listOf("arenatiltak"))
                it.demandKey("@løsning")
                it.requireKey("ident")
                it.requireKey("journalpostId")
                it.requireKey("@opprettet")
                it.interestedIn("@løsning.arenatiltak.tiltaksaktiviteter")
                it.interestedIn("@løsning.arenatiltak.feil")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        LOG.info("Received arenatiltak")
        SECURELOG.info("Received arenatiltak for ident id: ${packet["ident"].asText()}")

        //Metrics.mottakskanalInc(packet["mottaksKanal"].asText())

        val tiltak =
            if (packet["@løsning.arenatiltak.tiltaksaktiviteter"].asText() == "null")
                null
            else packet["@løsning.arenatiltak.tiltaksaktiviteter"]
        val ident = packet["ident"].asText()
        val journalpostId = packet["journalpostId"].asText()
        val innhentet = packet["@opprettet"].asLocalDateTime()
        val feil = packet["@løsning.arenatiltak.feil"].asText(null)

        vedtakClient.mottaTiltak(
            ArenaTiltakMottattDTO(
                tiltak = tiltak.asList(),
                ident = ident,
                journalpostId = journalpostId,
                innhentet = innhentet,
                feil = feil
            )
        )
    }

    fun JsonNode?.asList(): List<TiltaksaktivitetDTO> {
        var javaType: CollectionType = objectMapper.getTypeFactory()
            .constructCollectionType(List::class.java, TiltaksaktivitetDTO::class.java)

        return objectMapper.treeToValue(this, javaType)
    }
}
