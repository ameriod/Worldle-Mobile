object App {
    const val versionCode = 1
    const val versionName = "1.0.0"
}

object Versions {
    const val kotlin = "1.6.21"
    const val sqlDelight = "1.5.3"

    const val coroutines = "1.6.2"
    const val koin = "3.1.2"
    const val minSdk = 26
    const val compileSdk = 31
    const val targetSdk = 31

    const val kotlinxSerializationCore = "1.3.3"
    const val kotlinxCoroutinesCore = "${coroutines}-native-mt"
}

object Libraries {
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val kotlinSerialization = "org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlin}"
    const val sqlDelight = "com.squareup.sqldelight:gradle-plugin:${Versions.sqlDelight}"

    const val koinCore = "io.insert-koin:koin-core:${Versions.koin}"

    object Common {
        const val sqlDelight = "com.squareup.sqldelight:runtime:${Versions.sqlDelight}"
        const val sqlDelightExtension =
            "com.squareup.sqldelight:coroutines-extensions:${Versions.sqlDelight}"
        const val kotlinxSerializationCore =
            "org.jetbrains.kotlinx:kotlinx-serialization-core:${Versions.kotlinxSerializationCore}"
        const val kotlinxCoroutinesCore =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinxCoroutinesCore}"
    }

    object Android {
        const val sqlDelight = "com.squareup.sqldelight:android-driver:${Versions.sqlDelight}"
        const val coroutinesAndroid =
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
        const val koinAndroid = "io.insert-koin:koin-android:${Versions.koin}"
    }

    object iOS {
        const val sqlDelight = "com.squareup.sqldelight:native-driver:${Versions.sqlDelight}"
    }
}
