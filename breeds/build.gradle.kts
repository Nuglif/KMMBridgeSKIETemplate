plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqlDelight)
    alias(libs.plugins.android.library)
    `maven-publish`
}

kotlin {
    androidTarget {
        publishAllLibraryVariants()
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":analytics"))
            implementation(libs.coroutines.core)
            implementation(libs.bundles.ktor.common)
            implementation(libs.multiplatformSettings)
            implementation(libs.kotlinx.dateTime)
            implementation(libs.touchlab.kermit)
            implementation(libs.sqlDelight.coroutinesExt)
            implementation(libs.ktor.client.serialization)
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.client.core)
            implementation("io.ktor:ktor-client-serialization:2.3.11")
        }
        androidMain.dependencies {
            implementation(libs.sqlDelight.android)
            implementation(libs.ktor.client.okHttp)
        }
        iosMain.dependencies {
            implementation(libs.touchlab.stately.common)
            implementation(libs.sqlDelight.native)
            implementation(libs.ktor.client.ios)
            implementation("io.ktor:ktor-client-json:2.0.0")
        }
    }
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
    namespace = "co.touchlab.kmmbridgekickstart.breeds"
}

addGithubPackagesRepository()

sqldelight {
    databases.create("KMMBridgeKickStartDb") {
        packageName.set("co.touchlab.kmmbridgekickstart.db")
    }
}
