package com.nordeck.app.worldle

import android.app.Application
import coil.Coil
import coil.ImageLoader
import coil.decode.SvgDecoder

class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Coil.setImageLoader(ImageLoader.Builder(this)
            .components {
                add(SvgDecoder.Factory())
            }
            .build())
    }
}