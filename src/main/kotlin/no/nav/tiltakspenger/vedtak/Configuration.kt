package no.nav.tiltakspenger.vedtak

import com.natpryce.konfig.ConfigurationMap
import com.natpryce.konfig.ConfigurationProperties.Companion.systemProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.Key
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType
import no.nav.tiltakspenger.vedtak.oauth.AzureTokenProvider

object Configuration {

    val rapidsAndRivers = mapOf(
        "RAPID_APP_NAME" to "tiltakspenger-vedtak-rivers",
        "KAFKA_BROKERS" to System.getenv("KAFKA_BROKERS"),
        "KAFKA_CREDSTORE_PASSWORD" to System.getenv("KAFKA_CREDSTORE_PASSWORD"),
        "KAFKA_TRUSTSTORE_PATH" to System.getenv("KAFKA_TRUSTSTORE_PATH"),
        "KAFKA_KEYSTORE_PATH" to System.getenv("KAFKA_KEYSTORE_PATH"),
        "KAFKA_RAPID_TOPIC" to "tpts.rapid.v1",
        "KAFKA_RESET_POLICY" to "latest",
        "KAFKA_CONSUMER_GROUP_ID" to "tiltakspenger-skjerming-rivers-v2",
        "HTTP_PORT" to "8080",
    )

    private val otherDefaultProperties = mapOf(
        "application.httpPort" to 8080.toString(),
        "AZURE_APP_CLIENT_ID" to System.getenv("AZURE_APP_CLIENT_ID"),
        "AZURE_APP_CLIENT_SECRET" to System.getenv("AZURE_APP_CLIENT_SECRET"),
        "AZURE_APP_WELL_KNOWN_URL" to System.getenv("AZURE_APP_WELL_KNOWN_URL"),
        "logback.configurationFile" to "resources/logback.gcp.xml",
    )

    private val defaultProperties = ConfigurationMap(rapidsAndRivers + otherDefaultProperties)

    private val composeProperties = ConfigurationMap(
        mapOf(
            "logback.configurationFile" to "logback.local.xml",
        ),
    )

    private val localProperties = ConfigurationMap(
        mapOf(
            "application.httpPort" to 8082.toString(),
            "application.profile" to Profile.LOCAL.toString(),
            "vedtakScope" to "api://dev-gcp.tpts.tiltakspenger-vedtak/.default",
            "vedtakBaseUrl" to "http://localhost:8080",
            "dokumentScope" to "api://dev-gcp.tpts.tiltakspenger-dokument/.default",
            "dokumentBaseUrl" to "http://localhost:8087",
            "logback.configurationFile" to "src/main/resources/logback.local.xml",
        ),
    )
    private val devProperties = ConfigurationMap(
        mapOf(
            "application.profile" to Profile.DEV.toString(),
            "vedtakScope" to "api://dev-gcp.tpts.tiltakspenger-vedtak/.default",
            "vedtakBaseUrl" to "http://tiltakspenger-vedtak",
        ),
    )
    private val prodProperties = ConfigurationMap(
        mapOf(
            "application.profile" to Profile.PROD.toString(),
            "vedtakScope" to "api://prod-gcp.tpts.tiltakspenger-vedtak/.default",
            "vedtakBaseUrl" to "http://tiltakspenger-vedtak",
            "dokumentScope" to "api://prod-gcp.tpts.tiltakspenger-dokument/.default",
            "dokumentBaseUrl" to "http://tiltakspenger-dokument",
        ),
    )

    private fun config() = when (System.getenv("NAIS_CLUSTER_NAME") ?: System.getProperty("NAIS_CLUSTER_NAME")) {
        "dev-gcp" ->
            systemProperties() overriding EnvironmentVariables overriding devProperties overriding defaultProperties

        "compose" ->
            systemProperties() overriding EnvironmentVariables overriding composeProperties overriding defaultProperties

        "prod-gcp" ->
            systemProperties() overriding EnvironmentVariables overriding prodProperties overriding defaultProperties

        else -> {
            systemProperties() overriding EnvironmentVariables overriding localProperties overriding defaultProperties
        }
    }

    fun logbackConfigurationFile() = config()[Key("logback.configurationFile", stringType)]

    fun vedtakScope() = config()[Key("vedtakScope", stringType)]
    fun vedtakBaseUrl() = config()[Key("vedtakBaseUrl", stringType)]

    fun dokumentScope() = config()[Key("dokumentScope", stringType)]
    fun dokumentBaseUrl() = config()[Key("dokumentBaseUrl", stringType)]

    fun oauthConfig(
        scope: String,
        clientId: String = config()[Key("AZURE_APP_CLIENT_ID", stringType)],
        clientSecret: String = config()[Key("AZURE_APP_CLIENT_SECRET", stringType)],
        wellknownUrl: String = config()[Key("AZURE_APP_WELL_KNOWN_URL", stringType)],
    ) = AzureTokenProvider.OauthConfig(
        scope = scope,
        clientId = clientId,
        clientSecret = clientSecret,
        wellknownUrl = wellknownUrl,
    )

    fun clientConfig(baseUrl: String) =
        ClientConfig(baseUrl = baseUrl)

    fun applicationProfile() = when (System.getenv("NAIS_CLUSTER_NAME") ?: System.getProperty("NAIS_CLUSTER_NAME")) {
        "dev-gcp" -> Profile.DEV
        "prod-gcp" -> Profile.PROD
        else -> Profile.LOCAL
    }

    fun kafkaBootstrapLocal(): String = config()[Key("KAFKA_BROKERS", stringType)]
}

data class ClientConfig(
    val baseUrl: String,
)

enum class Profile {
    LOCAL, DEV, PROD
}
