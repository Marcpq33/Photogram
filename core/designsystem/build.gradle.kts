plugins {
    id("photogram.android.library")
    id("photogram.android.compose")
}

android {
    namespace = "com.photogram.core.designsystem"
}

dependencies {
    implementation(libs.compose.material.icons.extended)
}
