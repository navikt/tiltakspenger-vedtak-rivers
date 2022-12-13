package no.nav.tiltakspenger.vedtak.rivers

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.type.CollectionType
import mu.KotlinLogging
import net.logstash.logback.argument.StructuredArguments
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.tiltakspenger.vedtak.defaultObjectMapper

private val LOG = KotlinLogging.logger {}
private val SECURELOG = KotlinLogging.logger("tjenestekall")


val objectMapper = defaultObjectMapper()

inline fun <reified T> JsonNode?.asList(): List<T> {
    var javaType: CollectionType = objectMapper.getTypeFactory()
        .constructCollectionType(List::class.java, T::class.java)

    return objectMapper.treeToValue(this, javaType)
}

inline fun <reified T> JsonNode?.asObject(clazz: Class<T>): T = objectMapper.treeToValue(this, clazz)


fun loggVedInngang(behov: String, packet: JsonMessage) {
    LOG.info(
        "mottar løsning for $behov med {} og {}",
        StructuredArguments.keyValue("id", packet["@id"].asText()),
        StructuredArguments.keyValue("behovId", packet["@behovId"].asText())
    )
    SECURELOG.info(
        "mottar løsning for $behov med {} og {}",
        StructuredArguments.keyValue("id", packet["@id"].asText()),
        StructuredArguments.keyValue("behovId", packet["@behovId"].asText())
    )
    SECURELOG.debug { "mottok melding: ${packet.toJson()}" }
}

fun loggVedUtgang(behov: String, packet: JsonMessage) {
    LOG.info(
        "har mottatt løsning for $behov med {} og {}",
        StructuredArguments.keyValue("id", packet["@id"].asText()),
        StructuredArguments.keyValue("behovId", packet["@behovId"].asText())
    )
    SECURELOG.info(
        "har mottatt løsning for $behov med {} og {}",
        StructuredArguments.keyValue("id", packet["@id"].asText()),
        StructuredArguments.keyValue("behovId", packet["@behovId"].asText())
    )
    SECURELOG.debug { "publiserer melding: ${packet.toJson()}" }
}

fun loggVedFeil(behov: String, ex: Throwable, packet: JsonMessage) {
    LOG.error(
        "feil ved behandling av løsning for behov $behov med id {}, se securelogs for detaljer",
        StructuredArguments.keyValue("id", packet["@id"].asText()),
    )
    SECURELOG.error(
        "feil ${ex.message} ved behandling av løsning for behov $behov med {} og {}",
        StructuredArguments.keyValue("id", packet["@id"].asText()),
        StructuredArguments.keyValue("packet", packet.toJson()),
        ex
    )
}
