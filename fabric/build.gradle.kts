import net.fabricmc.loom.api.ModSettings

plugins {
    `java-library`
    id("net.fabricmc.fabric-loom-remap") version "1.14-SNAPSHOT"
    `maven-publish`
}

base {
    archivesName = "slate"
}

repositories {
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
    api(project(":common"))
    minecraft("com.mojang:minecraft:1.21.11")
    mappings(loom.officialMojangMappings())
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
    withType()

    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand(mapOf("version" to inputs.properties["version"]))
        }
    }

    jar {
        inputs.property("archivesName", project.base.archivesName)

        from("LICENSE") {
            rename { "${it}_${inputs.properties["archivesName"]}" }
        }
    }

    test {
        useJUnitPlatform()
    }
}