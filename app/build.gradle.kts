plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" // this version matches your Kotlin version
}

android {
    namespace = "com.example.mesportwe"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mesportwe"
        minSdk = 28
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx.v1131)
    implementation(libs.androidx.lifecycle.runtime.ktx.v270)
    implementation(libs.androidx.activity.compose.v190)
    implementation(platform(libs.androidx.compose.bom.v20240500))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)

    //implementation("androidx.compose.material:material:1.4.2")
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material3.android)
    // view model
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    // iconos
    implementation(libs.androidx.material.icons.extended.android)
    // rebote, no funciona
    implementation(libs.accompanist.insets)
    // cambiar barra de tareas
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    //implementation(libs.firebase.auth.ktx)
    //implementation(libs.firebase.perf.ktx)
    // ocultar top bar
    // implementation("com.google.accompanist:accompanist-coil:0.16.0")
    // animaciones nav host
    // implementation("com.google.accompanist:accompanist-navigation-animation:0.23.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.v121)
    androidTestImplementation(libs.androidx.espresso.core.v361)

    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    implementation(platform(libs.firebase.bom))
    implementation("com.google.firebase:firebase-analytics")
    // implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-auth:21.1.0")
    implementation("com.google.firebase:firebase-firestore:25.1.1")

    implementation("androidx.compose.runtime:runtime-livedata:1.6.6")
    implementation("androidx.datastore:datastore-preferences:1.1.0")

    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    implementation("com.maxkeppeler.sheets-compose-dialogs:core:1.0.2")
    implementation("com.maxkeppeler.sheets-compose-dialogs:clock:1.0.2")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")

}