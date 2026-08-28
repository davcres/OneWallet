import java.util.Properties

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false

    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.detekt)
}

tasks.register<Copy>("installGitHooks") {
    from(file("${rootProject.rootDir}/.githooks"))
    into(file("${rootProject.rootDir}/.git/hooks"))
    fileMode = 493 // 0755 in octal
}

afterEvaluate {
    tasks.named("prepareKotlinBuildScriptModel") {
        dependsOn("installGitHooks")
    }
}

// Cargar secrets.properties si existe
val secretsFile = rootProject.file("secrets.properties")

if (secretsFile.exists()) {
    val props = Properties().apply {
        load(secretsFile.inputStream())
    }

    // Copiamos todas las propiedades a `extra` del rootProject
    props.forEach { (k, v) ->
        extra[k.toString()] = v
    }
} else {
    logger.lifecycle("No secrets.properties file found. You can create one in the root project.")
}