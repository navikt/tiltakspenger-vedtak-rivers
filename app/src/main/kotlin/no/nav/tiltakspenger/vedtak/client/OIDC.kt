package no.nav.tiltakspenger.vedtak.client

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.http.Parameters

class OIDCIssuer private constructor(
    internal val client: HttpClient,
    internal val metadata: OIDCIssuerMetadata,
) {
    fun client(metadata: OIDCClientMetadata): OIDCClient = OIDCClient(this, metadata)

    companion object {
        suspend fun discover(issuer: String, block: () -> HttpClientEngine): OIDCIssuer {
            val client = HttpClient(engine = block()) { json() }
            val metadata = client.get("$issuer/.well-known/openid-configuration") {
                json()
            }.body<OIDCIssuerMetadata>()
            return OIDCIssuer(client, metadata)
        }
    }
}

class OIDCClient internal constructor(
    private val issuer: OIDCIssuer,
    private val metadata: OIDCClientMetadata,
) {
    suspend fun grant(grantType: String = "client_credentials"): OIDCTokenSet = issuer.client
        .submitForm(
            url = issuer.metadata.tokenEndpoint,
            formParameters = Parameters.build {
                append("grant_type", grantType)
                append("client_id", metadata.clientId)
                append("client_secret", metadata.clientSecret)
                append("scope", metadata.scope)
            },
        )
        .body()
}

data class OIDCIssuerMetadata(
    @JsonProperty("token_endpoint") val tokenEndpoint: String,
    @JsonAnySetter @get:JsonAnyGetter val other: Map<String, Any> = linkedMapOf(),
)

data class OIDCClientMetadata(
    val clientId: String,
    val clientSecret: String,
    val scope: String,
)

data class OIDCTokenSet(
    @JsonProperty("access_token") val accessToken: String,
    @JsonAnySetter @get:JsonAnyGetter val other: Map<String, Any> = linkedMapOf(),
) {
    @JsonIgnore
    fun toBearerTokens(): BearerTokens = BearerTokens(accessToken, "")
}

fun Auth.oidc(client: OIDCClient) {
    bearer {
        loadTokens {
            client.grant("client_credentials").toBearerTokens()
        }
        refreshTokens {
            client.grant("client_credentials").toBearerTokens()
        }
        sendWithoutRequest { true }
    }
}