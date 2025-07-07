plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

task stage(dependsOn: ['build'])

repositories {
    mavenCentral()

    maven {
        setUrl("https://maven.lavalink.dev/releases")
    }
    maven {
        setUrl("https://jitpack.io")
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("net.dv8tion:JDA:5.6.1")
    implementation("dev.arbjerg:lavaplayer:2.2.4")
    implementation("dev.lavalink.youtube:common:1.13.3")

    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("org.xerial:sqlite-jdbc:3.45.1.0")

    implementation("ch.qos.logback:logback-classic:1.5.18")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
}

tasks.test {
    useJUnitPlatform()
}
