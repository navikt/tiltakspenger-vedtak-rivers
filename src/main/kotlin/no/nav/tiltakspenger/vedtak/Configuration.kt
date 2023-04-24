package no.nav.tiltakspenger.vedtak

import com.natpryce.konfig.ConfigurationMap
import com.natpryce.konfig.ConfigurationProperties.Companion.systemProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.Key
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType
import no.nav.tiltakspenger.vedtak.client.VedtakClient
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
        "KAFKA_CONSUMER_GROUP_ID" to "tiltakspenger-skjerming-rivers-v1",
    )

    private val otherDefaultProperties = mapOf(
        "application.httpPort" to 8080.toString(),
        "AZURE_APP_CLIENT_ID" to System.getenv("AZURE_APP_CLIENT_ID"),
        "AZURE_APP_CLIENT_SECRET" to System.getenv("AZURE_APP_CLIENT_SECRET"),
        "AZURE_APP_WELL_KNOWN_URL" to System.getenv("AZURE_APP_WELL_KNOWN_URL"),
        "logback.configurationFile" to "resources/logback.gcp.xml",
    )
    private val defaultProperties = ConfigurationMap(rapidsAndRivers + otherDefaultProperties)

    private val localProperties = ConfigurationMap(
        mapOf(
            "application.profile" to Profile.LOCAL.toString(),
            "vedtakScope" to "api://dev-gcp.tpts.tiltakspenger-vedtak/.default",
            "vedtakBaseUrl" to "https://tiltakspenger-vedtak",
            "logback.configurationFile" to "resources/logback.local.xml",
        ),
    )
    private val devProperties = ConfigurationMap(
        mapOf(
            "application.profile" to Profile.DEV.toString(),
            "vedtakScope" to "api://dev-gcp.tpts.tiltakspenger-vedtak/.default",
            "vedtakBaseUrl" to "https://tiltakspenger-vedtak.intern.dev.nav.no",
        ),
    )
    private val prodProperties = ConfigurationMap(
        mapOf(
            "application.profile" to Profile.PROD.toString(),
            "vedtakScope" to "api://prod-gcp.tpts.tiltakspenger-vedtak/.default",
            "vedtakBaseUrl" to "https://tiltakspenger-vedtak.intern.nav.no",
        ),
    )

    private fun config() = when (System.getenv("NAIS_CLUSTER_NAME") ?: System.getProperty("NAIS_CLUSTER_NAME")) {
        "dev-gcp" ->
            systemProperties() overriding EnvironmentVariables overriding devProperties overriding defaultProperties

        "prod-gcp" ->
            systemProperties() overriding EnvironmentVariables overriding prodProperties overriding defaultProperties

        else -> {
            systemProperties() overriding EnvironmentVariables overriding localProperties overriding defaultProperties
        }
    }

    fun logbackConfigurationFile() = config()[Key("logback.configurationFile", stringType)]

    fun oauthConfig(
        scope: String = config()[Key("vedtakScope", stringType)],
        clientId: String = config()[Key("AZURE_APP_CLIENT_ID", stringType)],
        clientSecret: String = config()[Key("AZURE_APP_CLIENT_SECRET", stringType)],
        wellknownUrl: String = config()[Key("AZURE_APP_WELL_KNOWN_URL", stringType)],
    ) = AzureTokenProvider.OauthConfig(
        scope = scope,
        clientId = clientId,
        clientSecret = clientSecret,
        wellknownUrl = wellknownUrl,
    )

    fun vedtakClientConfig(baseUrl: String = config()[Key("vedtakBaseUrl", stringType)]) =
        VedtakClient.VedtakClientConfig(baseUrl = baseUrl)
}

enum class Profile {
    LOCAL, DEV, PROD
}
