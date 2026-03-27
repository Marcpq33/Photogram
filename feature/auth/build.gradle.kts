import java.util.Properties

plugins {
    id("photogram.android.feature")
    id("photogram.android.hilt")
    id("photogram.android.compose")
}

val localProperties = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) }
}

android {
    namespace = "com.photogram.feature.auth"
    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        // Google Web Client ID (OAuth 2.0 Web client) from Google Cloud Console.
        // Must match the client ID configured in Supabase → Authentication → Providers → Google.
        // Empty-string fallback is safe — signInWithGoogle() checks and returns a user-visible
        // error before attempting Credential Manager if this is blank.
        buildConfigField(
            "String", "GOOGLE_WEB_CLIENT_ID",
            "\"${localProperties.getProperty("GOOGLE_WEB_CLIENT_ID", "")}\"",
        )
    }
}

dependencies {
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.compose.material.icons.extended)
    // Credential Manager — Google Sign-In (already declared in version catalog)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.google.identity.googleid)
    implementation(project(":core:common"))
    implementation(project(":core:datastore"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))
    implementation(project(":core:network"))
}
