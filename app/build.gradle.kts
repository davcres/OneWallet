plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"
}

// Generate in /app/build/compose_metrics/*-composables.txt → list of all @Composable functions, marking if they are restartable/skippable/readonly and their params stability.
composeCompiler {
    reportsDestination.set(layout.buildDirectory.dir("compose_metrics"))
    metricsDestination.set(layout.buildDirectory.dir("compose_metrics"))
}

// Intentamos leer primero de secrets.properties (root extra), luego de env var (para CI)
val finnhubApiKey: String = (rootProject.extra["FINNHUB_API_KEY"] as? String)
    ?: System.getenv("FINNHUB_API_KEY")
    ?: throw GradleException(
        "FINNHUB_API_KEY not set. " +
                "Add it to secrets.properties (root) or as env var FINNHUB_API_KEY"
    )
val alphaVantageApiKey: String = (rootProject.extra["ALPHA_VANTAGE_API_KEY"] as? String)
    ?: System.getenv("ALPHA_VANTAGE_API_KEY")
    ?: throw GradleException(
        "ALPHA_VANTAGE_API_KEY not set. " +
                "Add it to secrets.properties (root) or as env var ALPHA_VANTAGE_API_KEY"
    )
val alphaVantageApiKey2: String = (rootProject.extra["ALPHA_VANTAGE_API_KEY_2"] as? String)
    ?: System.getenv("ALPHA_VANTAGE_API_KEY_2")
    ?: throw GradleException(
        "ALPHA_VANTAGE_API_KEY_2 not set. " +
                "Add it to secrets.properties (root) or as env var ALPHA_VANTAGE_API_KEY_2"
    )
val alphaVantageApiKey3: String = (rootProject.extra["ALPHA_VANTAGE_API_KEY_3"] as? String)
    ?: System.getenv("ALPHA_VANTAGE_API_KEY_3")
    ?: throw GradleException(
        "ALPHA_VANTAGE_API_KEY_3 not set. " +
                "Add it to secrets.properties (root) or as env var ALPHA_VANTAGE_API_KEY_3"
    )
val twelveDataApiKey: String = (rootProject.extra["TWELVE_DATA_API_KEY"] as? String)
    ?: System.getenv("TWELVE_DATA_API_KEY")
    ?: throw GradleException(
        "TWELVE_DATA_API_KEY not set. " +
                "Add it to secrets.properties (root) or as env var TWELVE_DATA_API_KEY"
    )
val telegramApiKey: String = (rootProject.extra["TELEGRAM_API_KEY"] as? String)
    ?: System.getenv("TELEGRAM_API_KEY")
    ?: throw GradleException(
        "TELEGRAM_API_KEY not set. " +
                "Add it to secrets.properties (root) or as env var TELEGRAM_API_KEY"
    )
val telegramChatId: String = (rootProject.extra["TELEGRAM_CHAT_ID"] as? String)
    ?: System.getenv("TELEGRAM_CHAT_ID")
    ?: throw GradleException(
        "TELEGRAM_CHAT_ID not set. " +
                "Add it to secrets.properties (root) or as env var TELEGRAM_CHAT_ID"
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

        buildConfigField("String", "FINNHUB_API_KEY", "\"$finnhubApiKey\"")
        buildConfigField("String", "ALPHA_VANTAGE_API_KEY", "\"$alphaVantageApiKey\"")
        buildConfigField("String", "ALPHA_VANTAGE_API_KEY_2", "\"$alphaVantageApiKey2\"")
        buildConfigField("String", "ALPHA_VANTAGE_API_KEY_3", "\"$alphaVantageApiKey3\"")
        buildConfigField("String", "TWELVE_DATA_API_KEY", "\"$twelveDataApiKey\"")
        buildConfigField("String", "TELEGRAM_API_KEY", "\"$telegramApiKey\"")
        buildConfigField("String", "TELEGRAM_CHAT_ID", "\"$telegramChatId\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    testOptions {
        animationsDisabled = true
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
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.foundation) // HorizontalPager
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    
    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

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

    // Glance (Widgets)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)

    // Navigation
    implementation(libs.bundles.navigation)

    // Work Manager
    implementation(libs.work.runtime.ktx)
    implementation(libs.koin.androidx.workmanager)

    // Immutable List
    implementation(libs.colletions.immutable)

    // Lottie
    implementation(libs.lottie.compose)

    // Splash Screen
    implementation(libs.core.splashscreen)
}