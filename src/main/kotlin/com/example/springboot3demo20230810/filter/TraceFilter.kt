package com.example.springboot3demo20230810.filter

import com.example.springboot3demo20230810.components.thread_local.TraceIdContext
import com.example.springboot3demo20230810.util.UuidUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.AbstractRequestLoggingFilter

// 在header增加"trace-id"用来追踪请求
@Order(10)
@Component
class TraceFilter : AbstractRequestLoggingFilter() {

//    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val traceId = UuidUtil.generateTraceId()

        logger.info("trace 请求开始, trace id: $traceId")

        response.setHeader("trace-id", traceId)
        TraceIdContext.setTraceId(traceId)

        filterChain.doFilter(request, response)

        TraceIdContext.clearTraceId()
    }

    // TODO: before 和 after 不生效
    override fun beforeRequest(request: HttpServletRequest, message: String) {
        val traceId = UuidUtil.generateTraceId()
        logger.info("trace 请求开始, trace id: $traceId")

        TraceIdContext.setTraceId(traceId)
    }

    override fun afterRequest(request: HttpServletRequest, message: String) {
        val traceId = TraceIdContext.getTraceId()

        logger.info("trace 请求结束, trace id: $traceId")
        TraceIdContext.clearTraceId()
    }
}