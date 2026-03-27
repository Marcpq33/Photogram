plugins {
    id("photogram.android.library")
    id("photogram.android.hilt")
}

android {
    namespace = "com.photogram.core.datastore"
}

dependencies {
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.core)
}
