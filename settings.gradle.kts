pluginManagement {
    includeBuild("build-logic")
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

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
    }
}

rootProject.name = "photogram"

// ── Modules ───────────────────────────────────────────────────────────────────
include(":app")

// ── Core ──────────────────────────────────────────────────────────────────────
include(":core:common")
include(":core:model")
include(":core:ui")
include(":core:designsystem")
include(":core:navigation")
include(":core:network")
include(":core:database")
include(":core:datastore")
include(":core:media")
include(":core:notifications")

// ── Features ──────────────────────────────────────────────────────────────────
include(":feature:auth")
include(":feature:home")
include(":feature:albums")
include(":feature:gallery")
include(":feature:upload")
include(":feature:story")
include(":feature:chat")
include(":feature:recaps")
include(":feature:events")
include(":feature:profile")
include(":feature:notifications")
include(":feature:settings")
include(":feature:privacy")
