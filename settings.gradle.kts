rootProject.name = "slate"

include("bukkit")
include("common")
include("fabric")

pluginManagement {
    repositories {
        maven {
            name = "Fabric"
            setUrl("https://maven.fabricmc.net/")
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
