plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.detekt)
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    baseline = file("detekt-baseline.xml")
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    source.setFrom("src/commonMain/kotlin", "src/jvmMain/kotlin", "src/jvmTest/kotlin")
}

kotlin {
    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.coroutines.test)
            implementation(libs.kotlinx.datetime)
        }

        jvmMain.dependencies {
            implementation(libs.junit.jupiter.api)
        }

        jvmTest.dependencies {
            implementation(libs.bundles.unit.testing)
            runtimeOnly(libs.junit.jupiter.engine)
        }
    }
}

tasks.named<Test>("jvmTest") {
    useJUnitPlatform()
}
