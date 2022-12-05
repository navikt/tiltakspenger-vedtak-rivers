package no.nav.tiltakspenger.vedtak.client

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import com.fasterxml.jackson.module.kotlin.treeToValue
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.http.HttpMessageBuilder
import io.ktor.http.contentType
import io.ktor.serialization.jackson.jackson
import java.util.*

val jsonMapper: JsonMapper = jacksonMapperBuilder()
    .addModule(JavaTimeModule())
    .build()

fun JsonNode.asUUID(): UUID = UUID.fromString(asText())

inline fun <reified T> JsonNode.asObject(): T = jsonMapper.treeToValue(this)

fun <T : HttpClientEngineConfig> HttpClientConfig<T>.json() {
    install(ContentNegotiation) {
        jackson {
            registerModule(JavaTimeModule())
        }
    }
}

fun HttpMessageBuilder.json() {
    accept(ContentType.Application.Json)
    contentType(ContentType.Application.Json)
}