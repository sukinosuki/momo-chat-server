package com.example.springboot3demo20230810.components.chat

import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

val sessionPool = ConcurrentHashMap<String, WebSocketSession>()
