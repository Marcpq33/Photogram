plugins {
    id("photogram.android.feature")
    id("photogram.android.hilt")
    id("photogram.android.compose")
}

android {
    namespace = "com.photogram.feature.settings"
}

dependencies {
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.compose.material.icons.extended)
    implementation(project(":core:datastore"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))
}
