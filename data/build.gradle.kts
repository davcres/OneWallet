plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.detekt)
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    baseline = file("detekt-baseline.xml")
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    source.setFrom("src/commonMain/kotlin", "src/androidMain/kotlin", "src/iosMain/kotlin", "src/androidUnitTest/kotlin")
}

val finnhubApiKey: String = (rootProject.extra["FINNHUB_API_KEY"] as? String)
    ?: System.getenv("FINNHUB_API_KEY")
    ?: throw GradleException("FINNHUB_API_KEY not set. Add it to secrets.properties (root) or as env var FINNHUB_API_KEY")

val alphaVantageApiKey: String = (rootProject.extra["ALPHA_VANTAGE_API_KEY"] as? String)
    ?: System.getenv("ALPHA_VANTAGE_API_KEY")
    ?: throw GradleException("ALPHA_VANTAGE_API_KEY not set. Add it to secrets.properties (root) or as env var ALPHA_VANTAGE_API_KEY")

val alphaVantageApiKey2: String = (rootProject.extra["ALPHA_VANTAGE_API_KEY_2"] as? String)
    ?: System.getenv("ALPHA_VANTAGE_API_KEY_2")
    ?: throw GradleException("ALPHA_VANTAGE_API_KEY_2 not set. Add it to secrets.properties (root) or as env var ALPHA_VANTAGE_API_KEY_2")

val alphaVantageApiKey3: String = (rootProject.extra["ALPHA_VANTAGE_API_KEY_3"] as? String)
    ?: System.getenv("ALPHA_VANTAGE_API_KEY_3")
    ?: throw GradleException("ALPHA_VANTAGE_API_KEY_3 not set. Add it to secrets.properties (root) or as env var ALPHA_VANTAGE_API_KEY_3")

val marketstackApiKey: String = (rootProject.extra["MARKETSTACK_API_KEY"] as? String)
    ?: System.getenv("MARKETSTACK_API_KEY")
    ?: throw GradleException("MARKETSTACK_API_KEY not set. Add it to secrets.properties (root) or as env var MARKETSTACK_API_KEY")

val marketstackApiKey2: String = (rootProject.extra["MARKETSTACK_API_KEY_2"] as? String)
    ?: System.getenv("MARKETSTACK_API_KEY_2")
    ?: throw GradleException("MARKETSTACK_API_KEY_2 not set. Add it to secrets.properties (root) or as env var MARKETSTACK_API_KEY_2")

val twelveDataApiKey: String = (rootProject.extra["TWELVE_DATA_API_KEY"] as? String)
    ?: System.getenv("TWELVE_DATA_API_KEY")
    ?: throw GradleException("TWELVE_DATA_API_KEY not set. Add it to secrets.properties (root) or as env var TWELVE_DATA_API_KEY")

val telegramApiKey: String = (rootProject.extra["TELEGRAM_API_KEY"] as? String)
    ?: System.getenv("TELEGRAM_API_KEY")
    ?: throw GradleException("TELEGRAM_API_KEY not set. Add it to secrets.properties (root) or as env var TELEGRAM_API_KEY")

val telegramChatId: String = (rootProject.extra["TELEGRAM_CHAT_ID"] as? String)
    ?: System.getenv("TELEGRAM_CHAT_ID")
    ?: throw GradleException("TELEGRAM_CHAT_ID not set. Add it to secrets.properties (root) or as env var TELEGRAM_CHAT_ID")

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain"))
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.coroutines)
            implementation(libs.bundles.ktor)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.datetime)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.koin.android)
            implementation(libs.androidx.room.ktx)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        androidUnitTest.dependencies {
            implementation(libs.bundles.unit.testing)
            implementation(libs.multiplatform.settings.test)
            runtimeOnly(libs.junit.jupiter.engine)
        }
    }
}

android {
    namespace = "com.davidcrespo.onewallet.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 30
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "FINNHUB_API_KEY", "\"$finnhubApiKey\"")
        buildConfigField("String", "ALPHA_VANTAGE_API_KEY", "\"$alphaVantageApiKey\"")
        buildConfigField("String", "ALPHA_VANTAGE_API_KEY_2", "\"$alphaVantageApiKey2\"")
        buildConfigField("String", "ALPHA_VANTAGE_API_KEY_3", "\"$alphaVantageApiKey3\"")
        buildConfigField("String", "MARKETSTACK_API_KEY", "\"$marketstackApiKey\"")
        buildConfigField("String", "MARKETSTACK_API_KEY_2", "\"$marketstackApiKey2\"")
        buildConfigField("String", "TWELVE_DATA_API_KEY", "\"$twelveDataApiKey\"")
    }

    buildTypes {
        debug {
            buildConfigField("String", "TELEGRAM_API_KEY", "\"$telegramApiKey\"")
            buildConfigField("String", "TELEGRAM_CHAT_ID", "\"$telegramChatId\"")
        }
        release {
            buildConfigField("String", "TELEGRAM_API_KEY", "\"\"")
            buildConfigField("String", "TELEGRAM_CHAT_ID", "\"\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        buildConfig = true
    }
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosX64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
}
