package com.nordeck.app.worldle

import android.app.Application
import coil.Coil
import coil.ImageLoader
import coil.decode.SvgDecoder
import timber.log.Timber

class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        Coil.setImageLoader(ImageLoader.Builder(this)
            .components {
                add(SvgDecoder.Factory())
            }
            .build())
    }
}