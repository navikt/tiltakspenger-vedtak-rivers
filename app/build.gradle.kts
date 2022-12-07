val ktorVersion = "2.1.3"
val kotestVersion = "5.5.4"
val koCoroutinVersion = "1.6.4"
val mockkVersion = "1.13.3"
val testContainersVersion = "1.17.6"

plugins {
    application
}

application {
    mainClass.set("no.nav.tiltakspenger.vedtak.AppKt")
}

fun ktor(name: String) = "io.ktor:ktor-$name:$ktorVersion"

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:$koCoroutinVersion")

    implementation(ktor("client-core"))
    implementation(ktor("client-logging-jvm"))
    implementation(ktor("client-cio"))
    implementation(ktor("client-jackson"))
    implementation(ktor("client-auth"))
    implementation(ktor("client-mock"))
    implementation(ktor("client-content-negotiation"))
    implementation(ktor("utils"))
    implementation(ktor("serialization-jackson"))
    implementation(ktor("server-auth"))
    implementation(ktor("server-auth-jwt"))
    implementation(ktor("server-call-id"))
    implementation(ktor("server-call-logging"))
    implementation(ktor("server-content-negotiation"))
    implementation(ktor("server-core"))
    implementation(ktor("server-core-jvm"))
    implementation(ktor("server-cors"))
    implementation(ktor("server-default-headers-jvm"))
    implementation(ktor("server-host-common"))
    implementation(ktor("server-host-common-jvm"))
    implementation(ktor("server-netty"))
    implementation(ktor("server-netty-jvm"))

    implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")
    implementation("ch.qos.logback:logback-classic:1.4.5")
    implementation("net.logstash.logback:logstash-logback-encoder:7.2")
    implementation("com.papertrailapp:logback-syslog4j:1.0.0")
    implementation("com.github.navikt:rapids-and-rivers:2022112407251669271100.df879df951cf")
    implementation("com.natpryce:konfig:1.6.10.0")

    // Auth
    implementation("com.auth0:jwks-rsa:0.21.2")

    // DB
    implementation("org.flywaydb:flyway-core:9.8.3")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.postgresql:postgresql:42.5.1")
    implementation("com.github.seratch:kotliquery:1.9.0")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$koCoroutinVersion")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("io.mockk:mockk-dsl-jvm:$mockkVersion")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-json:$kotestVersion")
    testImplementation("io.kotest:kotest-extensions:$kotestVersion")
    testImplementation("org.skyscreamer:jsonassert:1.5.1")
    testImplementation("org.testcontainers:testcontainers:$testContainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testContainersVersion")
    testImplementation("org.testcontainers:postgresql:$testContainersVersion")
    // need quarkus-junit-4-mock because of https://github.com/testcontainers/testcontainers-java/issues/970
    testImplementation("io.quarkus:quarkus-junit4-mock:2.15.0.Final")
}
