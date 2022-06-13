package com.nordeck.app.worldle.model

import android.content.Context
import java.io.InputStream

interface FileLoader {

    fun getAllFilesInPath(path: String): Array<String>

    fun getInputStreamFromFile(fileName: String): InputStream

    fun getStringFromFile(fileName: String): String
}

class AssetsFileLoader(
    private val context: Context
) : FileLoader {

    override fun getAllFilesInPath(path: String): Array<String> = context.assets.list("")!!

    override fun getStringFromFile(fileName: String): String = getInputStreamFromFile(fileName)
        .bufferedReader()
        .use {
            it.readText()
        }

    override fun getInputStreamFromFile(fileName: String): InputStream =
        context.assets.open(fileName)
}
