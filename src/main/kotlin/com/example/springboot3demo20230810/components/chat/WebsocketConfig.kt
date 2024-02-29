package com.example.springboot3demo20230810.components.chat

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Component
@EnableWebSocket
class WebsocketConfig : WebSocketConfigurer {

    @Autowired
    lateinit var webSocketInterceptor: WebSocketInterceptor

    @Autowired
    lateinit var handler: WebSocketHandler

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry
            .addHandler(handler, "/ws")
            .setAllowedOrigins("*")
            .addInterceptors(webSocketInterceptor)
    }
}