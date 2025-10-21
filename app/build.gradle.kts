plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.androidx.navigation.safeargs)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.smartbandiot"
    compileSdk = 34

    buildFeatures{
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.example.smartbandiot"
        minSdk = 29
        targetSdk = 34
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
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.recyclerview) // Menggunakan alias baru untuk recyclerview

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // 🌟 Firebase
    implementation(platform(libs.firebase.bom)) // Menggunakan alias BOM
    implementation(libs.firebase.auth.ktx)
    implementation(libs.google.services.auth) // Untuk Google Sign-In

    // 🌟 Pustaka Pihak Ketiga
    implementation(libs.pinview)
    implementation(libs.segmented.button)
    implementation(libs.circular.progressbar)
    implementation(libs.chip.navigation.bar)
    implementation(libs.circle.image.view)

    // Hapus dependensi yang tidak diperlukan
    // Hapus: implementation("androidx.recyclerview:recyclerview:1.3.1")
    // Hapus: implementation("com.google.firebase:firebase-auth:24.0.1")
}