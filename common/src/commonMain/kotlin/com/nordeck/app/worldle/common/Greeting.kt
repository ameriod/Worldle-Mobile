package com.nordeck.app.worldle.common

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}
