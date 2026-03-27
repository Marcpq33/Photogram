plugins {
    id("photogram.android.feature")
    id("photogram.android.hilt")
    id("photogram.android.compose")
}

android {
    namespace = "com.photogram.feature.profile"
}

dependencies {
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.androidx.activity.compose)   // rememberLauncherForActivityResult + PickVisualMedia
    implementation(libs.coil.compose)                // AsyncImage for gallery preview
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))
    implementation(project(":core:datastore"))
}
