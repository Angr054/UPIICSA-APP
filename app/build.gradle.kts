import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    //Servicios de Google
    //id("com.google.gms.google-services")
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.areyesm.upiicsaapp"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.areyesm.upiicsaapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)

    // Librería de Navigation
    implementation (libs.androidx.navigation.compose)

    // BoM Firebase
    implementation(platform(libs.firebase.bom))

    // Dependency for the Firebase Authentication library
    implementation("com.google.firebase:firebase-auth")

    // Dependency for Google Sign-In
    // https://firebase.google.com/docs/auth/android/google-signin?hl=es-419
    implementation("com.google.android.gms:play-services-auth:21.4.0")

    // Dependency for the Cloud Firestore library
    implementation("com.google.firebase:firebase-firestore")

    // Dependencies for the Credential Manager libraries
    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // Dependency for Facebook Auth
    // https://developers.facebook.com/docs/facebook-login/android
    // https://firebase.google.com/docs/auth/android/facebook-login?hl=es-419
    implementation("com.facebook.android:facebook-login:latest.release")

    // Dependency for the Cloud Storage library
    implementation("com.google.firebase:firebase-storage")

    // Dependency for the Cloud Firestore library
    implementation("com.google.firebase:firebase-firestore")

    // Dependency for the Realtime Database library
    implementation("com.google.firebase:firebase-database")

    // Material Icons
    implementation (libs.androidx.compose.material.icons.core)

    // Coil
    implementation("io.coil-kt:coil-compose:2.6.0")

    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}