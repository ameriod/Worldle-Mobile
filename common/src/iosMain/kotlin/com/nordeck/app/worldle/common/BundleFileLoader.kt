package com.nordeck.app.worldle.common

import com.nordeck.app.worldle.common.model.FileLoader
import platform.Foundation.NSBundle
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.stringWithContentsOfFile

class BundleFileLoader : FileLoader {

    override fun getStringFromFile(fileName: String): String {
        val path = NSBundle.mainBundle.resourcePath + "/$fileName"
        return NSString.stringWithContentsOfFile(path, NSUTF8StringEncoding, null) ?: ""
    }
}