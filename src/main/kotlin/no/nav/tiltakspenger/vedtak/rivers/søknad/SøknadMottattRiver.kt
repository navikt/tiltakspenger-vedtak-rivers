package no.nav.tiltakspenger.vedtak.rivers.søknad

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import mu.KotlinLogging
import mu.withLoggingContext
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.tiltakspenger.vedtak.client.IVedtakClient
import no.nav.tiltakspenger.vedtak.rivers.asObject

private val LOG = KotlinLogging.logger {}
private val SECURELOG = KotlinLogging.logger("tjenestekall")

internal class SøknadMottattRiver(
    private val vedtakClient: IVedtakClient,
    rapidsConnection: RapidsConnection,
) : River.PacketListener {

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandValue("@event_name", "søknad_mottatt")
                it.requireKey("@id")
                it.requireKey("@opprettet")
                it.requireKey("søknad")
                it.requireKey("søknad.dokInfo.journalpostId")
                it.requireKey("søknad.personopplysninger.ident")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        runCatching {
            LOG.info("Received søknad")
            SECURELOG.info("Received søknad: ${packet.toJson()}")
            withLoggingContext(
                "id" to packet["@id"].asText(),
                "journalpostId" to packet["søknad.dokInfo.journalpostId"].asText(),
            ) {
                val journalpostId = packet["søknad.dokInfo.journalpostId"].asText()
                val dto = packet["søknad"].asObject(SøknadDTO::class.java)

                runBlocking(MDCContext()) {
                    vedtakClient.mottaSøknad(
                        søknadDTO = dto,
                        journalpostId = journalpostId,
                    )
                }
                LOG.info { "Søknad med journalpostId $journalpostId sendt ok til vedtak" }
            }
        }.onFailure {
            LOG.error { "Feil ved vedtak for søknad med journalpostid: ${packet["søknad.journalpostId"].asText()}" }
        }.getOrThrow()
    }
}
