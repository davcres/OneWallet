plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.detekt)
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    baseline = file("detekt-baseline.xml")
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
}

android {
    namespace = "com.davidcrespo.onewallet.di"
    compileSdk = 36

    defaultConfig {
        minSdk = 30
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

dependencies {
    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(project(":core"))
    implementation(project(":feature:portfolio"))
    implementation(project(":feature:market"))
    implementation(project(":feature:onboarding"))
    implementation(project(":feature:widget"))

    implementation(libs.androidx.core.ktx)

    // Koin
    implementation(libs.bundles.koin)
    implementation(libs.koin.androidx.workmanager)

    // Ktor
    implementation(libs.bundles.ktor)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    // Work Manager
    implementation(libs.work.runtime.ktx)

    testImplementation(libs.bundles.unit.testing)
    testRuntimeOnly(libs.junit.jupiter.engine)
}
