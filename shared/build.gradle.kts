plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.buildkonfig)
}

val appVersionName = providers.gradleProperty("app.versionName").get()

buildkonfig {
    packageName = "io.github.v2compose"
    defaultConfigs {
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "VERSION_NAME",
            "\"$appVersionName\""
        )
    }
}


kotlin {
    android {
        namespace = "io.github.v2compose.shared"
        compileSdk = 36
        minSdk = 26

        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
        }
        commonMain.dependencies {
            implementation(project(":logging"))
            implementation(project(":htmlText"))

            implementation(libs.runtime)
            implementation(libs.foundation)
            implementation(libs.material3)
            implementation(libs.material.icons.extended)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ui.tooling.preview)

            // KMP Lifecycle & Navigation
            implementation(libs.jetbrains.lifecycle.viewmodel.compose)
            implementation(libs.jetbrains.lifecycle.runtime.compose)
            api(libs.jetbrains.navigation.compose)

            // KMP Koin Compose
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            // Compose Multiplatform Resources
            api(libs.components.resources)

            // Ktor
            api(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.auth)

            // Fruit-KT KMP
            api(libs.fruit)
            implementation(libs.fruit.ktor)
            implementation(libs.ksoup)

            // DataStore KMP
            api(libs.androidx.datastore.preferences)

            // Paging
            api(libs.androidx.paging.common)
            api(libs.androidx.paging.compose)

            // Coil
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            implementation(libs.coil.gif)
            implementation(libs.coil.svg)
            implementation(libs.mikepenz.markdown)
            implementation(libs.mikepenz.markdown.m3)
            implementation(libs.mikepenz.markdown.coil3)
            api(libs.compose.webview)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ktor.client.mock)
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.browser)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.slf4j.android)
            implementation(libs.androidx.work.runtime.ktx)
            implementation(libs.koin.androidx.workmanager)
            implementation(libs.okhttp.logging)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", libs.fruit.ksp)
}

compose.resources {
    publicResClass = true
    packageOfResClass = "v2compose.shared.generated.resources"
    generateResClass = always
}

// 解决 KMP 下手动引入 KSP 源码导致的 Task 依赖校验问题
tasks.configureEach {
    if (name.startsWith("compileKotlin") || name.startsWith("ksp")) {
        if (name != "kspCommonMainKotlinMetadata") {
            dependsOn("kspCommonMainKotlinMetadata")
        }
    }
}

// Fix for missing outputDirectory in copyAndroidMainComposeResourcesToAndroidAssets when using android.kotlin.multiplatform.library
tasks.configureEach {
    if (name == "copyAndroidMainComposeResourcesToAndroidAssets") {
        try {
            val dirProp = this.property("outputDirectory") as DirectoryProperty
            dirProp.set(layout.buildDirectory.dir("intermediates/compose_fake_assets"))
        } catch (e: Exception) {
        }
    }
}
