plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
<<<<<<< HEAD
    alias(libs.plugins.kotlin.android)
=======
>>>>>>> 1568a8635dec4abfb33c9d264cab7cb31673fe40
}

android {
    namespace = "com.jasatobias.turnosapp"
<<<<<<< HEAD
    compileSdk = 34

    defaultConfig {
        applicationId = "com.jasatobias.turnosapp"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
=======
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.jasatobias.turnosapp"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

>>>>>>> 1568a8635dec4abfb33c9d264cab7cb31673fe40
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
<<<<<<< HEAD
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
=======
            optimization {
                enable = false
            }
>>>>>>> 1568a8635dec4abfb33c9d264cab7cb31673fe40
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
<<<<<<< HEAD
    kotlinOptions {
        jvmTarget = "11"
    }
=======
>>>>>>> 1568a8635dec4abfb33c9d264cab7cb31673fe40
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.11.0")

    implementation(platform("com.google.firebase:firebase-bom:34.19.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
<<<<<<< HEAD
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
=======
>>>>>>> 1568a8635dec4abfb33c9d264cab7cb31673fe40
}