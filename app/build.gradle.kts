plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"
}

// Intentamos leer primero de secrets.properties (root extra), luego de env var (para CI)
val twelveDataApiKey: String = (rootProject.extra["TWELVE_DATA_API_KEY"] as? String)
    ?: System.getenv("TWELVE_DATA_API_KEY")
    ?: throw GradleException(
        "TWELVE_DATA_API_KEY not set. " +
                "Add it to secrets.properties (root) or as env var TWELVE_DATA_API_KEY"
    )
val finnhubApiKey: String = (rootProject.extra["FINNHUB_API_KEY"] as? String)
    ?: System.getenv("FINNHUB_API_KEY")
    ?: throw GradleException(
        "FINNHUB_API_KEY not set. " +
                "Add it to secrets.properties (root) or as env var FINNHUB_API_KEY"
    )

android {
    namespace = "com.davidcrespo.onewallet"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.davidcrespo.onewallet"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "TWELVE_DATA_API_KEY", "\"$twelveDataApiKey\"")
        buildConfigField("String", "FINNHUB_API_KEY", "\"$finnhubApiKey\"")
        buildConfigField("String", "TWELVE_DATA_BASE_URL", "\"api.twelvedata.com\"")
        buildConfigField("String", "FINNHUB_BASE_URL", "\"finnhub.io/api/v1\"")
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
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Ktor
    implementation(libs.bundles.ktor)

    // Koin
    implementation(libs.bundles.koin)
}