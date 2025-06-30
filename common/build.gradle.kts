plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    api("org.spongepowered:configurate-yaml:4.2.0")
    api("net.kyori:adventure-api:4.17.0")
    api("net.kyori:adventure-text-minimessage:4.16.0")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}