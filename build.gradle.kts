plugins {
    java
    idea
}

repositories {
    mavenCentral()
}

dependencies {

}

allprojects {
    group = "dev.aurelium"
    version = project.property("projectVersion") as String
    description = "A configurable and concise inventory GUI framework for Bukkit"
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
