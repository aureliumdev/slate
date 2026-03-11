import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.api.ModSettings

val shade: Configuration by configurations.creating

plugins {
    `java-library`
    id("net.fabricmc.fabric-loom-remap") version "1.14-SNAPSHOT"
    `maven-publish`
    id("com.gradleup.shadow") version "9.3.1"
}

repositories {
    mavenCentral()
}

loom {
    splitEnvironmentSourceSets()

    mods {
        create("slate", fun ModSettings.() {
            sourceSet(sourceSets.main.get())
            sourceSet(sourceSets.named("client").get())
        })
    }
}

dependencies {
    minecraft("com.mojang:minecraft:1.21.11")
    mappings(loom.officialMojangMappings())
    api(project(":common"))
    shade(project(":common"))
    implementation("net.kyori:adventure-text-serializer-legacy:4.25.0")
    include("net.kyori:adventure-api:4.25.0")
    include("net.kyori:examination-api:1.3.0")
    include("net.kyori:examination-string:1.3.0")
    include("net.kyori:adventure-key:4.25.0")
    include("net.kyori:adventure-text-serializer-legacy:4.25.0")
    include("net.kyori:adventure-text-minimessage:4.16.0")
    modImplementation(include("net.kyori:adventure-platform-fabric:6.8.0")!!)
    modImplementation("net.fabricmc:fabric-loader:0.18.2")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.139.4+1.21.11")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    withSourcesJar()
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks {
    withType<ShadowJar> {
        configurations = listOf(shade)
        archiveClassifier.set("dev")
        archiveFileName.set(null as String?)

        dependencies {
            exclude(dependency("net.kyori:.*"))
        }
    }

    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand(mapOf("version" to inputs.properties["version"]))
        }
    }

    jar {
        archiveBaseName.set("slate")
        archiveClassifier.set("fabric")
    }

    remapJar {
        inputFile.set(shadowJar.get().archiveFile)
        archiveBaseName.set("slate")
        archiveClassifier.set("fabric")

        finalizedBy("copyJar")
    }

    register<Copy>("copyJar") {
        val projectVersion: String by project
        from("build/libs/slate-${projectVersion}-fabric.jar")
        into("../build/libs")
    }

    test {
        useJUnitPlatform()
    }
}

publishing {
    publications.create<MavenPublication>("mavenJava") {
        artifactId = "slate-fabric"

        from(components["java"])
    }
}
