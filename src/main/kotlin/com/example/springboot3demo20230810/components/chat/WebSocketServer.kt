package com.example.springboot3demo20230810.components.chat

import jakarta.websocket.OnClose
import jakarta.websocket.OnError
import jakarta.websocket.OnMessage
import jakarta.websocket.OnOpen
import jakarta.websocket.Session
import jakarta.websocket.server.PathParam
import org.slf4j.LoggerFactory
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

//@ServerEndpoint("/ws/{uid}")
//@Component
class WebSocketServer {
    val pool = ConcurrentHashMap<String, Session>()
    val logger = LoggerFactory.getLogger(WebSocketServer::class.java)

//    @Autowired
//    lateinit var jwtUtils: JWTUtils

    @OnOpen
    fun onOpen(session: Session, @PathParam("uid") uid: String) {
    logger.info("open")
//        val claims = jwtUtils.parseToken(token)
//        logger.info("on open ${claims.id}")

        logger.info("session is WebSocketSession ", session is WebSocketSession)
        if (session is WebSocketSession) {
            val uid = session.attributes.get("uid")
            logger.info("有连接进来了 $uid")
        }
        pool[uid] = session

        session.asyncRemote.sendText("new user in ${uid}")
    }
//    @OnOpen
//    fun onOpen(session: Session, @PathParam("uid") uid: String, @RequestParam("token") token: String) {
//
//        val claims = jwtUtils.parseToken(token)
//        logger.info("on open ${claims.id}")
//
//        pool[claims.id] = session
//
//        session.asyncRemote.sendText("new user in ${claims.id}")
//    }

    @OnMessage
    fun onMessage(msg: String, session: Session) {
        pool.forEach { (k, v) ->
            v.asyncRemote.sendText("$msg from $k")
        }
    }

    @OnClose
    fun onClose(session: Session, @PathParam("uid") uid: String) {
        pool.remove(uid)
        pool.forEach { (k, v) ->
            v.asyncRemote.sendText("$uid leave")
        }
    }

    @OnError
    fun onError(session: Session, error: Throwable) {
        logger.error("error: ${error.message}")
    }
}