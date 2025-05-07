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
    implementation(libs.androidx.runtime.livedata)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

//     QR SCAN
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")


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

// check version
    implementation("com.google.api-client:google-api-client:1.35.0")

    // Required JSON parser

    implementation("com.fasterxml.jackson.core:jackson-core:2.13.4")
    implementation("com.google.http-client:google-http-client-jackson2:1.43.3")
//    check version
    implementation("com.google.http-client:google-http-client-gson:1.43.3")
    implementation("com.google.http-client:google-http-client-android:1.43.3")


    implementation("com.google.android.gms:play-services-identity:18.1.0")

    // implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")

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
//     excel
    implementation("org.apache.commons:commons-lang3:3.17.0")
    implementation("org.apache.poi:poi:5.3.0")
    implementation("org.apache.poi:poi-ooxml:5.3.0")
    implementation("org.apache.xmlbeans:xmlbeans:5.3.0")
}
