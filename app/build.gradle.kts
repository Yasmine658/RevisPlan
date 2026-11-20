plugins {
    id("com.android.application")
}

android {
    namespace = "tn.tp.revisplan"
    compileSdk = 36

    defaultConfig {
        applicationId = "tn.tp.revisplan"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
}
