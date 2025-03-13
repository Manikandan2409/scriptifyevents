plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
    id("kotlin-kapt")
}

android {
    namespace = "com.heremanikandan.scriptifyevents"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.heremanikandan.scriptifyevents"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/NOTICE.md"
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/AL2.0"
            excludes += "/META-INF/LGPL2.1"
            excludes +="/META-INF/DEPENDENCIES"
        }
    }

}
//
dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.googleid)
    implementation(libs.firebase.database.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Jetpack Compose dependencies
    implementation("androidx.compose.ui:ui:1.7.8")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    // firebase OpenCredentials
    implementation("androidx.credentials:credentials:1.2.0-alpha04")
    implementation("androidx.credentials:credentials-play-services-auth:1.5.0-rc01")
    implementation("com.google.firebase:firebase-core:9.6.1")
    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    // Firebase BoM (for Firebase Auth if needed)
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    // mailer
    implementation ("com.sun.mail:android-mail:1.6.7")
    implementation("com.sun.mail:android-activation:1.6.7")
    // Google Drive API
    implementation("com.google.api-client:google-api-client-android:1.35.0")
    implementation("com.google.apis:google-api-services-drive:v3-rev197-1.25.0")
    // Gmail API
    implementation("com.google.apis:google-api-services-gmail:v1-rev110-1.25.0")
    // Google Calendar API
    //implementation("com.google.apis:google-api-services-calendar:v3-rev305-1.25.0")
    implementation ("com.google.apis:google-api-services-calendar:v3-rev20220715-2.0.0")
    // Google Sheets API
    implementation("com.google.apis:google-api-services-sheets:v4-rev612-1.25.0")
    // Google Slides API
//    implementation("com.google.apis:google-api-services-slides:v1-rev20220523-1.32.1")
    implementation("com.google.apis:google-api-services-slides:v1-rev20210820-1.32.1")

    // Compose Navigation
    //implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation ("androidx.navigation:navigation-compose:2.7.7")

    // Icons and Image Loading
    implementation ("androidx.compose.material:material-icons-extended:1.6.3")

    // Coil for Image Loading (optional for profile image)
    implementation("io.coil-kt:coil-compose:2.5.0")

    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1") // For annotation processing

    // Kotlin Extensions & Coroutines Support
    implementation("androidx.room:room-ktx:2.6.1")

    // Optional: If you use RxJava with Room
    implementation("androidx.room:room-rxjava3:2.6.1")

    // Optional: If you use Paging with Room
    implementation("androidx.room:room-paging:2.6.1")

}
//dependencies {
//    // Core & Lifecycle
//    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.lifecycle.runtime.ktx)
//    implementation(libs.androidx.activity.compose)
//
//    // Compose BOM (Handles versioning for Compose)
//    implementation(platform(libs.androidx.compose.bom))
//    implementation("androidx.compose.ui:ui")
//    implementation("androidx.compose.ui:ui-graphics")
//    implementation("androidx.compose.ui:ui-tooling-preview")
//    implementation("androidx.compose.material3:material3")
//
//    // Jetpack Compose Navigation
//    implementation("androidx.navigation:navigation-compose:2.7.7")
//
//    // Firebase
//    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
//    implementation("com.google.firebase:firebase-auth-ktx")
//    implementation(libs.firebase.database.ktx)
//
//    // Google Sign-In
//    implementation("com.google.android.gms:play-services-auth:20.7.0")
//
//    // Google APIs
//    implementation("com.google.apis:google-api-services-drive:v3-rev197-1.25.0")
//    implementation("com.google.apis:google-api-services-gmail:v1-rev110-1.25.0")
//    implementation("com.google.apis:google-api-services-calendar:v3-rev20220715-2.0.0")
//    implementation("com.google.apis:google-api-services-sheets:v4-rev612-1.25.0")
//   // implementation("com.google.apis:google-api-services-slides:v1-rev20220523-1.32.1") // ✅ Uncommented
//    implementation("com.google.apis:google-api-services-slides:v1-rev20210820-1.32.1")
//    // Jetpack Room
//    implementation("androidx.room:room-runtime:2.6.1")
//    kapt("androidx.room:room-compiler:2.6.1")
//    implementation("androidx.room:room-ktx:2.6.1")
//    implementation("androidx.room:room-paging:2.6.1")
//
//    // Image Loading (Coil)
//    implementation("io.coil-kt:coil-compose:2.5.0")
//
//    // Icons
//    implementation("androidx.compose.material:material-icons-extended:1.6.3")
//
//    // Testing
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
//    androidTestImplementation(platform(libs.androidx.compose.bom))
//    androidTestImplementation(libs.androidx.ui.test.junit4)
//    debugImplementation(libs.androidx.ui.tooling)
//    debugImplementation(libs.androidx.ui.test.manifest)
//}
