plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

fun String.toBuildConfigString(): String = "\"" +
    replace("\\", "\\\\").replace("\"", "\\\"") + "\""

val openAiBaseUrl = (project.findProperty("OPENAI_BASE_URL") as String?)
    ?: "https://open.bigmodel.cn/api/paas/v4/chat/completions"
val openAiModel = (project.findProperty("OPENAI_MODEL") as String?)
    ?: "gpt-4o-mini"
val openAiApiKey = (project.findProperty("OPENAI_API_KEY") as String?)
    ?: ""

android {
    namespace = "com.han.nomemo"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.han.nomemo"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "OPENAI_BASE_URL", openAiBaseUrl.toBuildConfigString())
        buildConfigField("String", "OPENAI_MODEL", openAiModel.toBuildConfigString())
        buildConfigField("String", "OPENAI_API_KEY", openAiApiKey.toBuildConfigString())
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
    buildFeatures {
        buildConfig = true
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.profileinstaller)
    implementation("io.coil-kt.coil3:coil-compose:3.4.0")
    implementation("me.saket.telephoto:zoomable-image-coil3:0.19.0")

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // Backdrop: Liquid Glass Effect Library
    implementation("io.github.kyant0:backdrop:2.0.0-alpha03")

    // Capsule: G2 Continuous Curve
    implementation("io.github.kyant0:capsule:2.1.3")
    
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.core:core:1.17.0")
    implementation("com.google.android.material:material:1.13.0")
    testImplementation(libs.junit)
    testImplementation("org.json:json:20240303")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
