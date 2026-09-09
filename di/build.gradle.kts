plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.detekt)
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    baseline = file("detekt-baseline.xml")
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    source.setFrom(
        files(
            "src/commonMain/kotlin",
            "src/androidMain/kotlin",
            "src/iosMain/kotlin",
            "src/androidUnitTest/kotlin"
        )
    )
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

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    iosArm64()
    iosSimulatorArm64()
    iosX64()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core"))
            implementation(project(":domain"))
            implementation(project(":data"))
            implementation(project(":feature:portfolio"))
            implementation(project(":feature:market"))
            implementation(project(":feature:onboarding"))

            implementation(compose.runtime)

            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.colletions.immutable)
            implementation(libs.kotlinx.datetime)
        }

        androidMain.dependencies {
            implementation(project(":feature:widget"))

            implementation(libs.androidx.core.ktx)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
            implementation(libs.koin.androidx.workmanager)
            implementation(libs.work.runtime.ktx)
        }

        iosMain.dependencies {
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(libs.bundles.unit.testing)
                runtimeOnly(libs.junit.jupiter.engine)
            }
        }
    }
}
