package com.nordeck.app.worldle.common

import android.content.Context
import com.nordeck.app.worldle.common.model.FileLoader

class AssetsFileLoader(
    private val context: Context
) : FileLoader {

    override fun getStringFromFile(fileName: String): String = context.assets.open(fileName)
        .bufferedReader()
        .use {
            it.readText()
        }
}
