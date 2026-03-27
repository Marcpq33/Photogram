import java.util.Properties

plugins {
    id("photogram.android.library")
    id("photogram.android.hilt")
}

val localProperties = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) }
}

android {
    namespace = "com.photogram.core.network"
    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        // Assumption: SUPABASE_URL and SUPABASE_ANON_KEY are set in local.properties.
        // Empty-string fallback is safe — Supabase client will be created but all calls fail
        // until real credentials are provided.
        buildConfigField(
            "String", "SUPABASE_URL",
            "\"${localProperties.getProperty("SUPABASE_URL", "")}\"",
        )
        buildConfigField(
            "String", "SUPABASE_ANON_KEY",
            "\"${localProperties.getProperty("SUPABASE_ANON_KEY", "")}\"",
        )
    }
}

dependencies {
    api(platform(libs.supabase.bom))
    api(libs.supabase.auth)
    implementation(libs.ktor.client.android)
}
