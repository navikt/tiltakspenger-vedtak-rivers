val javaVersion = JavaVersion.VERSION_17
val ktorVersion = "2.2.2"
val kotestVersion = "5.5.4"
val kotlinxCoroutinesVersion = "1.6.4"
val mockkVersion = "1.13.4"
val felleslibVersion = "0.0.21"

plugins {
    application
    kotlin("jvm") version "1.8.0"
    id("com.diffplug.spotless") version "6.13.0"
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://packages.confluent.io/maven/")
    maven("https://jitpack.io")
}

fun ktor(name: String) = "io.ktor:ktor-$name:$ktorVersion"

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:$kotlinxCoroutinesVersion")

    implementation("com.github.navikt.tiltakspenger-libs:arenatiltak-dtos:$felleslibVersion")
    implementation("com.github.navikt.tiltakspenger-libs:arenaytelser-dtos:$felleslibVersion")
    implementation("com.github.navikt.tiltakspenger-libs:person-dtos:$felleslibVersion")
    implementation("com.github.navikt.tiltakspenger-libs:skjerming-dtos:$felleslibVersion")
    implementation("com.github.navikt.tiltakspenger-libs:fp-dtos:$felleslibVersion")
    implementation("com.github.navikt.tiltakspenger-libs:ufore-dtos:$felleslibVersion")
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
    implementation("com.github.navikt:rapids-and-rivers:2022122311551671792919.2bdd972d7bdb")
    implementation("com.natpryce:konfig:1.6.10.0")
    implementation("com.auth0:jwks-rsa:0.21.3")

    testImplementation(platform("org.junit:junit-bom:5.9.2"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinxCoroutinesVersion")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("io.mockk:mockk-dsl-jvm:$mockkVersion")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-json:$kotestVersion")
    testImplementation("io.kotest:kotest-extensions:$kotestVersion")
    testImplementation("org.skyscreamer:jsonassert:1.5.1")
}

configurations.all {
    // exclude JUnit 4
    exclude(group = "junit", module = "junit")
}

application {
    mainClass.set("no.nav.tiltakspenger.vedtak.AppKt")
}

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}


spotless {
    kotlin {
        ktlint("0.45.2")
    }
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = javaVersion.toString()
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = javaVersion.toString()
        kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    }
    test {
        // JUnit 5 support
        useJUnitPlatform()
        // https://phauer.com/2018/best-practices-unit-testing-kotlin/
        systemProperty("junit.jupiter.testinstance.lifecycle.default", "per_class")
    }
}
