plugins {
    `kotlin-dsl`
}

group = "com.photogram.buildlogic"

kotlin {
    jvmToolchain(17)
}

dependencies {
    compileOnly("com.android.tools.build:gradle:${libs.versions.agp.get()}")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")
    compileOnly("com.google.devtools.ksp:symbol-processing-gradle-plugin:${libs.versions.ksp.get()}")
    compileOnly("com.google.dagger:hilt-android-gradle-plugin:${libs.versions.hilt.get()}")
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "photogram.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "photogram.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidFeature") {
            id = "photogram.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("androidHilt") {
            id = "photogram.android.hilt"
            implementationClass = "HiltConventionPlugin"
        }
        register("androidCompose") {
            id = "photogram.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("kotlinLibrary") {
            id = "photogram.kotlin.library"
            implementationClass = "KotlinLibraryConventionPlugin"
        }
    }
}
