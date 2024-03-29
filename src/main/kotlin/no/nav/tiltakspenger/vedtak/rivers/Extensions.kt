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

fun loggFeltVedInngang(behov: String, feltNavn: String, felt: String) {
    SECURELOG.info(
        "mottar løsning for $behov med {}",
        StructuredArguments.keyValue(feltNavn, felt),
    )
}

fun loggFeltVedUtgang(behov: String, feltNavn: String, felt: String) {
    SECURELOG.info(
        "har mottatt løsning for $behov med {}",
        StructuredArguments.keyValue(feltNavn, felt),
    )
}

fun loggFeltVedFeil(behov: String, ex: Throwable, feltNavn: String, felt: String) {
    SECURELOG.error(
        "feil ${ex.message} ved behandling av løsning for behov $behov med {}",
        StructuredArguments.keyValue(feltNavn, felt),
        ex,
    )
}

fun loggBehovVedInngang(behov: String, packet: JsonMessage) {
    LOG.info(
        "mottar løsning for $behov med {} og {}",
        StructuredArguments.keyValue("id", packet["@id"].asText()),
        StructuredArguments.keyValue("behovId", packet["@behovId"].asText()),
    )
    SECURELOG.info(
        "mottar løsning for $behov med {} og {}",
        StructuredArguments.keyValue("id", packet["@id"].asText()),
        StructuredArguments.keyValue("behovId", packet["@behovId"].asText()),
    )
    SECURELOG.debug { "mottok melding: ${packet.toJson()}" }
}

fun loggBehovVedUtgang(behov: String, packet: JsonMessage) {
    LOG.info(
        "har mottatt løsning for $behov med {} og {}",
        StructuredArguments.keyValue("id", packet["@id"].asText()),
        StructuredArguments.keyValue("behovId", packet["@behovId"].asText()),
    )
    SECURELOG.info(
        "har mottatt løsning for $behov med {} og {}",
        StructuredArguments.keyValue("id", packet["@id"].asText()),
        StructuredArguments.keyValue("behovId", packet["@behovId"].asText()),
    )
    SECURELOG.debug { "publiserer melding: ${packet.toJson()}" }
}

fun loggBehovVedFeil(behov: String, ex: Throwable, packet: JsonMessage) {
    LOG.error(
        "feil ved behandling av løsning for behov $behov med id {}, se securelogs for detaljer",
        StructuredArguments.keyValue("id", packet["@id"].asText()),
    )
    SECURELOG.error(
        "feil ${ex.message} ved behandling av løsning for behov $behov med {} og {}",
        StructuredArguments.keyValue("id", packet["@id"].asText()),
        StructuredArguments.keyValue("packet", packet.toJson()),
        ex,
    )
}

fun loggEventVedInngang(eventNavn: String, packet: JsonMessage) {
    LOG.info(
        "mottar event $eventNavn med {}",
        StructuredArguments.keyValue("id", packet["@id"].asText()),
    )
    SECURELOG.info(
        "mottar event $eventNavn med {}",
        StructuredArguments.keyValue("id", packet["@id"].asText()),
    )
    SECURELOG.debug { "mottok melding: ${packet.toJson()}" }
}

fun loggEventVedUtgang(eventNavn: String, packet: JsonMessage) {
    LOG.info(
        "har mottatt event $eventNavn med {}",
        StructuredArguments.keyValue("id", packet["@id"].asText()),
    )
    SECURELOG.info(
        "har mottatt event $eventNavn med {}",
        StructuredArguments.keyValue("id", packet["@id"].asText()),
    )
    SECURELOG.debug { "publiserer melding: ${packet.toJson()}" }
}

fun loggEventVedFeil(eventNavn: String, ex: Throwable, packet: JsonMessage) {
    LOG.error(
        "feil ved behandling av event $eventNavn med id {}, se securelogs for detaljer",
        StructuredArguments.keyValue("id", packet["@id"].asText()),
    )
    SECURELOG.error(
        "feil ${ex.message} ved behandling av event $eventNavn med {} og {}",
        StructuredArguments.keyValue("id", packet["@id"].asText()),
        StructuredArguments.keyValue("packet", packet.toJson()),
        ex,
    )
}
