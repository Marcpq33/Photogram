plugins {
    id("photogram.android.library")
    id("photogram.android.compose")
}

android {
    namespace = "com.photogram.core.navigation"
}

dependencies {
    api(libs.androidx.navigation.compose)
    api(libs.hilt.navigation.compose)
}
