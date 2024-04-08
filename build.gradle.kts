val javaVersion = JavaVersion.VERSION_21
val ktorVersion = "2.3.10"
val kotestVersion = "5.8.1"
val kotlinxCoroutinesVersion = "1.8.0"
val mockkVersion = "1.13.10"
val felleslibVersion = "0.0.89"

plugins {
    application
    kotlin("jvm") version "1.9.23"
    id("com.diffplug.spotless") version "6.25.0"
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://packages.confluent.io/maven/")
    maven {
        url = uri("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
    }
}

fun ktor(name: String) = "io.ktor:ktor-$name:$ktorVersion"

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:$kotlinxCoroutinesVersion")

    implementation("com.github.navikt.tiltakspenger-libs:tiltak-dtos:$felleslibVersion")
    implementation("com.github.navikt.tiltakspenger-libs:arenaytelser-dtos:$felleslibVersion")
    implementation("com.github.navikt.tiltakspenger-libs:person-dtos:$felleslibVersion")
    implementation("com.github.navikt.tiltakspenger-libs:skjerming-dtos:$felleslibVersion")
    implementation("com.github.navikt.tiltakspenger-libs:dokument-dtos:$felleslibVersion")
    implementation("com.github.navikt.tiltakspenger-libs:fp-dtos:$felleslibVersion")
    implementation("com.github.navikt.tiltakspenger-libs:ufore-dtos:$felleslibVersion")
    implementation("com.github.navikt.tiltakspenger-libs:overgangsstonad-dtos:$felleslibVersion")
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
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    implementation("ch.qos.logback:logback-classic:1.5.3")
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")
    implementation("com.papertrailapp:logback-syslog4j:1.0.0")
    implementation("com.github.navikt:rapids-and-rivers:2024022311041708682651.01821651ed22")
    implementation("com.natpryce:konfig:1.6.10.0")
    implementation("com.auth0:jwks-rsa:0.22.1")

    testImplementation(platform("org.junit:junit-bom:5.10.2"))
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
        ktlint("0.48.2")
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

task("addPreCommitGitHookOnBuild") {
    println("⚈ ⚈ ⚈ Running Add Pre Commit Git Hook Script on Build ⚈ ⚈ ⚈")
    exec {
        commandLine("cp", "./.scripts/pre-commit", "./.git/hooks")
    }
    println("✅ Added Pre Commit Git Hook Script.")
}
