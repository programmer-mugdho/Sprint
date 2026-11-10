pluginManagement {
  repositories {
    google {
      content {
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("androidx.*")
      }
    }
    mavenCentral()
    gradlePluginPortal()
  }
}

plugins { id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0" }

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
  }
}

rootProject.name = "Sprint"

val keystore = file("debug.keystore")
val base64 = file("debug.keystore.base64")
if (!keystore.exists() && base64.exists()) {
  try {
    val decoded = java.util.Base64.getDecoder().decode(base64.readText().trim())
    keystore.writeBytes(decoded)
  } catch (_: Exception) {}
}

include(":app")
