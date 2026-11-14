
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.tenski.ai"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.tenski.ai"
        minSdk = 27
        targetSdk = 28
        versionCode = 1
        versionName = "1.0"
    }
}
