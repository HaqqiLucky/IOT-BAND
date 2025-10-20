plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.androidx.navigation.safeargs)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.smartbandiot"
    compileSdk = 36

    buildFeatures{
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.example.smartbandiot"
        minSdk = 29
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
//    implementation(libs.firebase.database.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("io.github.chaosleung:pinview:1.4.4") // buat kotak pin phone verif
    implementation("com.github.addisonelliott:SegmentedButton:3.1.9") // segmented control ios height weight
    implementation("com.mikhaellopez:circularprogressbar:3.1.0") // loading bulet
    implementation("com.github.ismaeldivita:chip-navigation-bar:1.4.0") //navbar di main activity
    implementation("androidx.recyclerview:recyclerview:1.3.1") //recycleview
//    implementation(com.github.bumptech.glide:glide:4.16.0) //upload foto buat profil
    implementation("androidx.recyclerview:recyclerview:1.3.1") //recycleview
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("org.maplibre.gl:android-sdk:11.5.1")//maps

    // bombayah
    implementation(platform("com.google.firebase:firebase-bom:34.4.0"))

    // firebase login
    implementation("com.google.android.gms:play-services-auth:21.4.0")
    implementation("com.google.firebase:firebase-auth:24.0.1")

    // firebase realtime database
    implementation("com.google.firebase:firebase-database")



}