object App {
    const val versionCode = 1
    const val versionName = "1.0.0"
}

object Versions {
    const val kotlin = "1.6.21"
    const val sqlDelight = "1.5.3"

    const val coroutines = "1.6.2"
    const val coroutinesNative = "1.6.0-native-mt"
    const val koin = "3.1.2"
    const val minSdk = 26
    const val compileSdk = 31
    const val targetSdk = 31

    const val kotlinxSerializationCore = "1.3.3"
}

object Libraries {
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val kotlinSerialization = "org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlin}"
    const val sqlDelight = "com.squareup.sqldelight:gradle-plugin:${Versions.sqlDelight}"

    const val koinCore = "io.insert-koin:koin-core:${Versions.koin}"

    object Common {
        const val sqlDelight = "com.squareup.sqldelight:runtime:${Versions.sqlDelight}"
        const val sqlDelightCoroutinesExtension =
            "com.squareup.sqldelight:coroutines-extensions:${Versions.sqlDelight}"
        const val kotlinxSerializationCore =
            "org.jetbrains.kotlinx:kotlinx-serialization-core:${Versions.kotlinxSerializationCore}"
        const val KotlinxSerializationJson =
            "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerializationCore}"
        const val kotlinxCoroutinesCore =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    }

    object Android {
        const val sqlDelight = "com.squareup.sqldelight:android-driver:${Versions.sqlDelight}"
        const val coroutines =
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
        const val koin = "io.insert-koin:koin-android:${Versions.koin}"
    }

    object iOS {
        const val sqlDelight = "com.squareup.sqldelight:native-driver:${Versions.sqlDelight}"
        const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutinesNative}"
    }
}
