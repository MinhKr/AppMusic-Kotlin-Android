plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.minhldn.appmusic"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.minhldn.appmusic"
        minSdk = 24
        targetSdk = 35
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
        buildFeatures {
            dataBinding = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.material.v110beta01)
    implementation (libs.androidx.media)
    implementation (libs.androidx.lifecycle.viewmodel.ktx)
    implementation (libs.androidx.fragment.ktx)
    implementation (libs.androidx.lifecycle.runtime.ktx)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation (libs.okhttp)
}

kapt {
    correctErrorTypes = true
}