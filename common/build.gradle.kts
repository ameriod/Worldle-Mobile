import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jlleitschuh.gradle.ktlint")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.squareup.sqldelight")
}

kotlin {
    android()

    val xcf = XCFramework()
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "common"
            xcf.add(this)
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Libraries.Common.KotlinxSerializationJson)
                implementation(Libraries.Common.kotlinxSerializationCore)
                api(Libraries.Common.kotlinxCoroutinesCore)
                implementation(Libraries.Common.sqlDelight)
                api(Libraries.Common.sqlDelightCoroutinesExtension)
                api(Libraries.Common.koin)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                api(Libraries.Android.sqlDelight)
                api(Libraries.Android.coroutines)
                api(Libraries.Common.koin)
                api(Libraries.Android.koin)
            }
        }
        val androidTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation(Libraries.iOS.sqlDelight)
                implementation(Libraries.iOS.coroutines)
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    compileSdk = Versions.compileSdk
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk
    }
}

sqldelight {
    database("HistoryDatabase") {
        packageName = "com.nordeck.app.worldle.common"
    }
}
