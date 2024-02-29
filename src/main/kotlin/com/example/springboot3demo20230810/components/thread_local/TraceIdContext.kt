package com.example.springboot3demo20230810.components.thread_local

import org.slf4j.LoggerFactory
import org.slf4j.MDC

object TraceIdContext {
    private val logger = LoggerFactory.getLogger(TraceIdContext::class.java)

    private const val TRACE_ID_KEY = "trace-id"

    private const val TRACE_START_TIME_KEY = "trace_start_time"

    fun setTraceId(id: String) {
        MDC.put(TRACE_ID_KEY, id)
    }

    fun getTraceId(): String? {
        val traceId = MDC.get(TRACE_ID_KEY)

        return traceId
    }

    fun removeTraceId() {
        MDC.remove(TRACE_ID_KEY)
    }

    fun clearTraceId() {
        MDC.clear()
    }

    fun setTraceStartTime(timeStamp: Long) {
        MDC.put(TRACE_START_TIME_KEY, timeStamp.toString())
    }

    fun getTraceStartTime(): Long {
        val str = MDC.get(TRACE_START_TIME_KEY)

        return str.toLong()
    }
}