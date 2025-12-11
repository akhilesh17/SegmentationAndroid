@Suppress("UnstableApiUsage")
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.segmentation"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.segmentation"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        vectorDrawables.useSupportLibrary = true
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    packaging {
        resources {
            excludes += "/META-INF/{LICENSE,LICENSE.txt,NOTICE,NOTICE.txt}"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    aaptOptions {
        noCompress += "tflite"
    }
    configurations.all {
        exclude(group = "org.tensorflow", module = "tensorflow-lite")
        exclude(group = "org.tensorflow", module = "tensorflow-lite-gpu")
        exclude(group = "org.tensorflow", module = "tensorflow-lite-support")
        exclude(group = "org.tensorflow", module = "tensorflow-lite-api")
    }
}

dependencies {

    // ─────────────────────────────────────────────
    // AndroidX Core + Lifecycle (DOWNGRADED TO STABLE)
    // ─────────────────────────────────────────────
    // Was 1.17.0 (Requires SDK 36) -> Downgraded to 1.15.0
    implementation("androidx.core:core-ktx:1.15.0")

    // Was 2.10.0 (Requires SDK 36) -> Downgraded to 2.8.7
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    implementation(libs.kotlinx.coroutines.android)

    // ─────────────────────────────────────────────
    // Compose
    // ─────────────────────────────────────────────
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Was 1.12.0 (Requires SDK 36) -> Downgraded to 1.9.3
    implementation("androidx.activity:activity-compose:1.9.3")

    // ─────────────────────────────────────────────
    // CameraX
    // ─────────────────────────────────────────────
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // ─────────────────────────────────────────────
    // MediaPipe Tasks – Image Segmenter
    // ─────────────────────────────────────────────
    implementation("com.google.mediapipe:tasks-vision:0.10.9") // Change .10 to .9


    // ─────────────────────────────────────────────
    // Testing
    // ─────────────────────────────────────────────
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
