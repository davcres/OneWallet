plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.detekt)
}

apply(from = "jacoco.gradle")

detekt {
    buildUponDefaultConfig = true
    allRules = false
    baseline = file("detekt-baseline.xml")
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
}

// Generate in /app/build/compose_metrics/*-composables.txt → list of all @Composable functions, marking if they are restartable/skippable/readonly and their params stability.
composeCompiler {
    reportsDestination.set(layout.buildDirectory.dir("compose_metrics"))
    metricsDestination.set(layout.buildDirectory.dir("compose_metrics"))
}

android {
    namespace = "com.davidcrespo.onewallet"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.davidcrespo.onewallet"
        minSdk = 30
        targetSdk = 36
        versionCode = 2
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    testOptions {
        animationsDisabled = true
        unitTests.all {
            it.useJUnitPlatform()
        }
    }

    // Need to exclude it to maintain JUnit 4 for UI Tests (Standard for compose tests) having JUnit 5 for Unit Tests
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/LICENSE-notice.md"
        }
    }
}

dependencies {
    implementation(project(":di"))
    implementation(project(":core"))
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":feature:portfolio"))
    implementation(project(":feature:market"))
    implementation(project(":feature:onboarding"))
    implementation(project(":feature:widget"))

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

    // Testing
    testImplementation(libs.bundles.unit.testing)
    testRuntimeOnly(libs.junit.jupiter.engine)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.mockk.android)
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
    implementation(libs.okio) // Avoid old Okio 1.17.6 referenced by Lottie, which triggers R8 missing javax.annotation.Nullable

    // Splash Screen
    implementation(libs.core.splashscreen)

    // Onboarding
    implementation(libs.onboarding)

}
