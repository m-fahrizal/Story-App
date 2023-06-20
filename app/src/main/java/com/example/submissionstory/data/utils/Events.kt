package com.example.submissionstory.data.utils

open class Events<out T>(private val content: T) {
    private var isHandled = false

    fun getUnhandledContent(): T? {
        return if (isHandled) {
            null
        } else {
            isHandled = true
            content
        }
    }
}