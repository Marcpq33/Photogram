plugins {
    id("photogram.android.application")
    id("photogram.android.hilt")
    id("photogram.android.compose")
}

android {
    namespace = "com.photogram"
    defaultConfig {
        applicationId = "com.photogram"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Core
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(project(":core:datastore"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))
    implementation(project(":core:network"))   // SupabaseClient for session observation + deep-link handling

    // Features
    implementation(project(":feature:auth"))
    implementation(project(":feature:home"))
    implementation(project(":feature:albums"))
    implementation(project(":feature:gallery"))
    implementation(project(":feature:upload"))
    implementation(project(":feature:story"))
    implementation(project(":feature:chat"))
    implementation(project(":feature:recaps"))
    implementation(project(":feature:events"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:notifications"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:privacy"))
}
