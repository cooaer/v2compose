pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
rootProject.name = "V2compose"
val localFruitKt = listOf(file("fruit-kt"), file("../fruit-kt")).firstOrNull { it.isDirectory }
if (localFruitKt != null) {
    includeBuild(localFruitKt)
}
include(":androidApp")
include(":htmlText")
include(":logging")
include(":shared")
