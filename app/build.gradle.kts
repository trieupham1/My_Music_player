plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.tdtu.my_music_player"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.tdtu.my_music_player"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // Enable ProGuard for shrinking, obfuscation, and resource shrinking
            isMinifyEnabled = true
            isShrinkResources = true // This helps to remove unused resources
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
}

dependencies {
    // Firebase BOM to manage all Firebase versions
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))

    // Firebase Authentication library
    implementation("com.google.firebase:firebase-auth")

    // Firebase Analytics and Firestore, if applicable
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")

    // Other dependencies (AndroidX, UI components, etc.)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.material)
    implementation(libs.appcompat)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation ("androidx.media:media:1.6.0")
    implementation ("androidx.core:core-ktx:1.10.1")

}

// Apply the Google Services plugin at the end of the file
apply(plugin = "com.google.gms.google-services")
