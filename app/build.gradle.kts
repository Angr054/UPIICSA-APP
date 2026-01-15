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
    implementation(libs.firebase.auth)

    // Dependency for Google Sign-In
    // https://firebase.google.com/docs/auth/android/google-signin?hl=es-419
    implementation(libs.play.services.auth)

    // Dependency for the Cloud Firestore library
    implementation(libs.firebase.firestore)

    // Dependencies for the Credential Manager libraries
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    // Dependency for Facebook Auth
    // https://developers.facebook.com/docs/facebook-login/android
    // https://firebase.google.com/docs/auth/android/facebook-login?hl=es-419
    implementation(libs.facebook.login)

    // Dependency for the Cloud Storage library
    implementation(libs.firebase.storage)

    // Dependency for the Cloud Firestore library
    implementation(libs.google.firebase.firestore)

    // Dependency for the Realtime Database library
    implementation(libs.firebase.database)

    // Material Icons
    implementation (libs.androidx.compose.material.icons.core)

    // Coil
    implementation(libs.coil.compose)

    // Dependency for SplashScreen
    implementation(libs.androidx.core.splashscreen)

    //Dependency for location
    implementation(libs.play.services.location)

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