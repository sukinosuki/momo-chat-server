package com.example.springboot3demo20230810.util

fun <T> toCatch(fn: () -> T): Pair<T?, Exception?> {
    return try {
        val res = fn()

        Pair(res, null)
    } catch (e: Exception) {

        Pair(null, e)
    }
}