plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
}

// 1. 定义判断逻辑：是否包含 Google 相关的构建任务
val isGoogleTask = gradle.startParameter.taskRequests.any { request ->
    request.args.any { it.contains("Google", ignoreCase = true) }
}

// 2. 只有在执行 Google 相关任务时才动态应用插件
if (isGoogleTask) {
    apply(plugin = libs.plugins.google.services.get().pluginId)
    apply(plugin = libs.plugins.crashlytics.get().pluginId)

    tasks.matching {
        it.name.endsWith("GoogleServices") && !it.name.startsWith("processGoogle")
    }.configureEach {
        enabled = false
    }
    tasks.matching {
        it.name.contains("Crashlytics") && !it.name.contains("Google")
    }.configureEach {
        enabled = false
    }
}

val appVersionCode = providers.gradleProperty("app.versionCode").get().toInt()
val appVersionName = providers.gradleProperty("app.versionName").get()

val releaseSigningEnvVars = listOf(
    "ANDROID_RELEASE_KEYSTORE_PATH",
    "ANDROID_RELEASE_KEYSTORE_PASSWORD",
    "ANDROID_RELEASE_KEY_ALIAS",
    "ANDROID_RELEASE_KEY_PASSWORD"
)
val isReleaseBuildTask = gradle.startParameter.taskRequests.any { request ->
    request.args.any { arg ->
        val taskName = arg.substringAfterLast(':')
        taskName.contains("Release") &&
            (taskName.startsWith("assemble") || taskName.startsWith("bundle"))
    }
}
val missingReleaseSigningEnvVars = releaseSigningEnvVars.filter {
    providers.environmentVariable(it).orNull.isNullOrBlank()
}
if (isReleaseBuildTask && missingReleaseSigningEnvVars.isNotEmpty()) {
    throw GradleException(
        "Release signing environment variables are missing: " +
            missingReleaseSigningEnvVars.joinToString()
    )
}

android {
    namespace = "io.github.v2compose"
    compileSdk = 36

    defaultConfig {
        applicationId = "io.github.v2compose"
        minSdk = 26
        targetSdk = 35
        versionCode = appVersionCode
        versionName = appVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            providers.environmentVariable("ANDROID_RELEASE_KEYSTORE_PATH").orNull?.let {
                storeFile = file(it)
            }
            storePassword = providers.environmentVariable("ANDROID_RELEASE_KEYSTORE_PASSWORD").orNull
            keyAlias = providers.environmentVariable("ANDROID_RELEASE_KEY_ALIAS").orNull
            keyPassword = providers.environmentVariable("ANDROID_RELEASE_KEY_PASSWORD").orNull
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    flavorDimensions += "tracking"
    productFlavors {
        create("foss") {
            dimension = "tracking"
            multiDexEnabled = true
        }
        create("google") {
            dimension = "tracking"
            multiDexEnabled = true
        }
    }
}

dependencies {
    implementation(project(":shared"))

    // Jetpack
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    // Dependency Injection
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.workmanager)

    implementation(libs.androidx.core.splashscreen)

    implementation(libs.androidx.browser)
    implementation(libs.androidx.startup.runtime)
    implementation(libs.androidx.work.runtime.ktx)

    // compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)

    // firebase (flavor specific)
    "googleImplementation"(platform(libs.firebase.bom))
    "googleImplementation"(libs.firebase.crashlytics)
    "googleImplementation"(libs.firebase.analytics)

    // test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // image loading
    implementation(libs.coil)
}


// Workaround: Add Compose Resources generated in shared KMP module correctly structured to app assets
android {
    sourceSets.getByName("main") {
        val sharedDir =
            project(":shared").layout.buildDirectory.dir("intermediates/compose_fake_assets")
                .get().asFile.absolutePath
        assets.directories.add(sharedDir)
    }
}

val copySharedComposeResourcesToAndroidAssets = project(":shared").tasks.matching {
    it.name == "copyAndroidMainComposeResourcesToAndroidAssets"
}

tasks.matching {
    (it.name.startsWith("merge") && it.name.endsWith("Assets")) ||
        (it.name.startsWith("generate") && it.name.endsWith("LintVitalReportModel")) ||
        it.name.startsWith("lintVitalAnalyze")
}.configureEach {
    dependsOn(copySharedComposeResourcesToAndroidAssets)
}
