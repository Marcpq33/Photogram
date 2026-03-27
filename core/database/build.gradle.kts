plugins {
    id("photogram.android.library")
    id("photogram.android.hilt")
}

android {
    namespace = "com.photogram.core.database"
}

dependencies {
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
}
