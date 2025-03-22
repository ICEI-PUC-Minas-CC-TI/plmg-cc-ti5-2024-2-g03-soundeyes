plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id ("com.google.dagger.hilt.android")
}

android {
    namespace = "com.jerson.soundeyes"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.jerson.soundeyes"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "2.0"

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
    kotlinOptions {
        jvmTarget = "11"
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
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    //tensorFlow
    implementation (libs.tensorflow.lite)
    implementation (libs.tensorflow.lite.support)
    implementation (libs.tensorflow.lite.gpu)
    implementation (libs.tensorflow.lite.gpu.api)


    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation (libs.androidx.navigation.compose)
    implementation (libs.androidx.material.icons.extended)

    //Accompanist permissions
    implementation ("com.google.accompanist:accompanist-permissions:0.28.0")
    //Coil
    implementation("io.coil-kt:coil-compose:2.1.0")

    //Dagger Hilt
    implementation( "com.google.dagger:hilt-android:2.51.1")
    ksp("com.google.dagger:hilt-compiler:2.51.1")
    implementation ("androidx.hilt:hilt-navigation-compose:1.2.0")


    //Room
    implementation ("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")


    // Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // CameraX Core Library
    implementation( "androidx.camera:camera-core:1.3.4")
    implementation ("androidx.camera:camera-camera2:1.3.4")
    // CameraX Lifecycle
    implementation ("androidx.camera:camera-lifecycle:1.3.4")
    // CameraX View
    implementation ("androidx.camera:camera-view:1.3.4")
}