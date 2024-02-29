package com.example.springboot3demo20230810.components.chat

import com.example.springboot3demo20230810.modules.student.StudentLoginCache
import com.example.springboot3demo20230810.util.JWTUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.http.server.ServletServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor
import java.lang.Exception

// 在ws握手连接里校验token, 如果校验失败或者与缓存token不一致则拒绝连接
@Component
class WebSocketInterceptor : HandshakeInterceptor {
    private val logger = LoggerFactory.getLogger(this::class.java)
    val protocolHeaderName = "Sec-WebSocket-Protocol"

    @Autowired
    lateinit var jwtUtil: JWTUtil

    @Autowired
    lateinit var loginCache: StudentLoginCache

    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>
    ): Boolean {
        logger.info("ws握手开始")

        if (request is ServletServerHttpRequest) {
            val authorization = request.servletRequest.getHeader(protocolHeaderName)

            logger.info("ws握手开始, authorization: $authorization")

            if (authorization == null) {
                logger.warn("ws握手开始, authorization为空, 拒绝连接")
                response.setStatusCode(HttpStatus.UNAUTHORIZED)
                return false
            }

            // 校验token是否有效、过期
            val (claims, exception) = jwtUtil.parseToken(authorization)
            if (exception != null) {
                logger.warn("ws握手开始, 解析token失败, 拒绝连接, err: $exception")
                return false
            }

            // 连接ws判断token与缓存token是否一致, 不一致则拒绝连接.
            // 不一致的原因是 1.token过期, 2.断开ws连接超过一定时间,缓存token已经清空或者被其他用户登录了(当前为ws断开连接后设置token5分钟过期)
            logger.info("ws握手开始, 解析token成功 $claims")

            val (exist, cachedToken) = loginCache.exist(claims!!.subject.toLong())

            if (!exist || cachedToken != authorization) {
                logger.warn("ws握手开始, ws连接对象token缓存不存在或者不一致, 拒绝连接")
                return false
            }

            attributes.put(AttributeKey_Uid, claims.subject)
        }

        return true
    }

    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?
    ) {
        val req = (request as ServletServerHttpRequest).servletRequest
        val res = (response as ServletServerHttpResponse).servletResponse
        val header = req.getHeader(protocolHeaderName)

        logger.info("握手结束 header $header")

        header?.let {
            res.addHeader(protocolHeaderName, header)
        }

        logger.info("握手结束")
    }
}