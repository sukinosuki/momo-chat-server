package com.example.springboot3demo20230810.util

import java.util.UUID

object UuidUtil {

    fun generateTraceId(): String {
        val uuid = UUID.randomUUID().toString().replace("-", "")

//        return uuid.substring(0, 16)
        return uuid
    }
}