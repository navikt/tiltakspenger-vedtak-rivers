package no.nav.tiltakspenger.vedtak.domene

data class Saksbehandler(val navIdent: String, val brukernavn: String, val epost: String, val roller: List<Rolle>)
