import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.milas.rageshield"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.milas.rageshield"
        minSdk = 26
        targetSdk = 35
        // CI run number keeps increasing, so each new build installs as an update.
        versionCode = System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull() ?: 1
        versionName = "0.1.0"
    }

    // One permanent key for every build, so new APKs install over the old one
    // (no uninstall needed). This is a debug-grade key kept in the repo on purpose.
    signingConfigs {
        getByName("debug") {
            storeFile = file("rageshield-debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2025.11.00"))
    implementation("androidx.activity:activity-compose:1.12.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
