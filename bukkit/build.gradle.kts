import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    id("com.gradleup.shadow") version "8.3.5"
    idea
    `maven-publish`
    signing
}

java {
    withJavadocJar()
    withSourcesJar()
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://repo.helpch.at/releases/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    api(project(":common"))
    api("org.spongepowered:configurate-yaml:4.2.0")
    api("net.kyori:adventure-platform-bukkit:4.3.2")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.mojang:authlib:1.5.25")
    compileOnly("org.jetbrains:annotations:24.1.0")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks {
    withType<ShadowJar> {
        val projectVersion: String by project
        archiveFileName.set("slate-${projectVersion}-bukkit.jar")

        exclude("plugin.yml")

        finalizedBy("copyJar")
    }

    register<Copy>("copyJar") {
        val projectVersion: String by project
        from("build/libs/slate-${projectVersion}-bukkit.jar")
        into("../build/libs")
    }

    javadoc {
        title = "Slate API (${project.version})"
        source = sourceSets.main.get().allSource
        classpath = files(sourceSets.main.get().compileClasspath)
        options {
            (this as CoreJavadocOptions).addStringOption("Xdoclint:none", "-quiet")
            overview("javadoc/overview.html")
            encoding("UTF-8")
            charset("UTF-8")
        }
    }

    build {
        dependsOn(shadowJar)
        dependsOn(javadoc)
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    test {
        useJUnitPlatform()
    }
}

if (project.properties.keys.containsAll(setOf("developerId", "developerUsername", "developerEmail", "developerUrl"))) {
    publishing {
        repositories {
            maven {
                name = "StagingDeploy"
                url = uri(layout.buildDirectory.dir("staging-deploy"))
            }
            if (project.properties.keys.containsAll(setOf("sonatypeUsername", "sonatypePassword")) && project.version.toString().endsWith("-SNAPSHOT")) {
                maven {
                    name = "Snapshot"
                    url = uri("https://central.sonatype.com/repository/maven-snapshots/")

                    credentials {
                        username = project.property("sonatypeUsername").toString()
                        password = project.property("sonatypePassword").toString()
                    }
                }
            }
        }

        publications.create<MavenPublication>("mavenJava") {
            groupId = "dev.aurelium"
            artifactId = "slate"
            version = project.version.toString()

            pom {
                name.set("Slate")
                description.set("API for building user-configurable GUI menus")
                url.set("https://wiki.aurelium.dev/slate")
                licenses {
                    license {
                        name.set("The GNU General Public License, Version 3.0")
                        url.set("https://www.gnu.org/licenses/gpl-3.0.en.html")
                    }
                }
                developers {
                    developer {
                        id.set(project.property("developerId").toString())
                        name.set(project.property("developerUsername").toString())
                        email.set(project.property("developerEmail").toString())
                        url.set(project.property("developerUrl").toString())
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/Archy-X/Slate.git")
                    developerConnection.set("scm:git:git://github.com/Archy-X/Slate.git")
                    url.set("https://github.com/Archy-X/Slate/tree/master")
                }
            }

            from(components["java"])
        }
    }

    signing {
        sign(publishing.publications.getByName("mavenJava"))
        isRequired = true
    }
}
