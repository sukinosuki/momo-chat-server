package com.example.springboot3demo20230810.components.chat

import com.example.springboot3demo20230810.entity.ChatMessage
import com.example.springboot3demo20230810.model.Message
import com.example.springboot3demo20230810.model.MessageActionType
import com.example.springboot3demo20230810.model.MessageContentType
import com.example.springboot3demo20230810.modules.chat_message.ChatMessageRepository
import com.example.springboot3demo20230810.modules.student.StudentLoginCache
import com.example.springboot3demo20230810.modules.student.StudentUnreadCache
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import kotlin.math.max
import kotlin.math.min

@Component
class WebSocketHandler : WebSocketHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var chatMessageRepository: ChatMessageRepository

    @Autowired
    lateinit var studentUnreadCache: StudentUnreadCache

    @Autowired
    lateinit var loginCache: StudentLoginCache

    @Autowired
    lateinit var rabbitMqChatMessageProvider: RabbitMqChatMessageProvider

    fun getUid(session: WebSocketSession): Long {
        val uid = session.attributes.get(AttributeKey_Uid) ?: return 0

        return uid.toString().toLong()
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        logger.info("ws 连接成功 ")
//        val uid = session.attributes.get("uid")
        val uid = getUid(session)
        logger.info("uid $uid")
        logger.info("session id ${session.id}")

        val message = Message(
            type = MessageActionType.UserOnline,
            fromSid = uid,
            msgType = MessageContentType.Text,
            message = "$uid online"
        )
        val mapper = jacksonObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)

        sessionPool.put(uid.toString(), session)
        sessionPool.forEach { (k, s) ->
            if (s.isOpen) {
                s.sendMessage(TextMessage(mapper.writeValueAsString(message)))
            }
        }

        logger.info("当前在线人数: ${sessionPool.size}")

        // 连接成功, 重置token缓存时间
        loginCache.setExpire(uid, 60 * 60 * 24)
    }

    // handle received message
    override fun handleMessage(session: WebSocketSession, webSocketMessage: WebSocketMessage<*>) {
        val mapper = jacksonObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)

        val message = mapper.readValue(webSocketMessage.payload.toString(), Message::class.java)

        logger.info("收到消息 message: $message ")
        val uid = getUid(session)
        // TODO: 校验参数

        // 校验1: 发送人id需要和token的id一致
        if (message.fromSid != uid) {
            // TODO:
            message.fromSid = uid
            return
        }

        when (message.type) {
            // 处理读取消息(消息动作类型为读取消息
            MessageActionType.ReadAll -> {
                studentUnreadCache.clearUnreadCount(uid, message.toSid)

                return
            }

            // 处理心跳
            MessageActionType.PingPong -> {
                sessionPool.get(uid.toString())?.sendMessage(TextMessage("pong"))
                return
            }

            MessageActionType.Normal -> {
                // 交由下游处理
            }

            // 不处理其它action type的消息
            else -> {
                return
            }
        }

        // 增加未读数, 自己发给自己不需要增加未读数
        if (uid != message.toSid) {
            studentUnreadCache.increaseUnreadCount(message.fromSid, message.toSid)
        }

        // 发送消息给目标
        if (message.toSid.toInt() == 0) {
            sessionPool.forEach { (k, session) ->
                if (session.isOpen) {
                    session.sendMessage(TextMessage(mapper.writeValueAsString(message)))
                }
            }
        } else {
            sessionPool[message.toSid.toString()]?.sendMessage(TextMessage(mapper.writeValueAsString(message)))
        }

        // 响应发送消息pong(如果前端需要做单个消息loading需要该响应
        sessionPool[uid.toString()]?.let { session ->
            val copiedMessage = message.copy(type = MessageActionType.SendPong)
            session.sendMessage(TextMessage(mapper.writeValueAsString(copiedMessage)))
        }

        // TODO: 储存消息交给消息队列
        // 储存message
        val chatMessage = ChatMessage(
            message = message.message,
            type = message.type,
            msgType = message.msgType,
            fromSid = message.fromSid,
            toSid = message.toSid,
            sidMin = min(message.fromSid, message.toSid),
            sidMax = max(message.fromSid, message.toSid),
        )

//        // 通过rabbitmq消息中间件存数据库
//        rabbitMqChatMessageProvider.sendChatMessage(chatMessage)

        // 直接存数据库
        chatMessageRepository.save(chatMessage)
    }

    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        logger.info("连接出错 $exception")
//        val uid = session.attributes.get("uid")
        val uid = getUid(session)
        session.close()
//        if (uid == null) return

        sessionPool.remove(uid.toString())

        logger.info("当前在线: ${sessionPool.size}")
        val message = Message(
            type = MessageActionType.UserOffline,
            fromSid = uid,
            msgType = MessageContentType.Text,
            message = "$uid offline"
        )
        val mapper = jacksonObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)

        sessionPool.forEach { (k, s) ->
            s.sendMessage(TextMessage(mapper.writeValueAsString(message)))
        }
    }

    // 连接关闭
    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        val fromSid = getUid(session)
        logger.info("关闭连接 , id: ${session.id}, uid: $fromSid")
//        if (fromSid == null) return

        sessionPool.remove(fromSid.toString())

        logger.info("当前在线: ${sessionPool.size}")
        val message = Message(
            type = MessageActionType.UserOffline,
            fromSid = fromSid,
            msgType = MessageContentType.Text,
            message = "$fromSid offline"
        )
        val mapper = jacksonObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)

        sessionPool.forEach { (k, s) ->
            s.sendMessage(TextMessage(mapper.writeValueAsString(message)))
        }

        // 用户关闭ws连接时，减小登录token缓存时间让其他用户可以登录, 这个时间不能太短, 以便让当前用户可以重新连接
        // 设置5分钟有效期, 如果当前用户在该时间段里没有重新连接ws, token会清空, 其他用户在登录时可以选择该学生
        loginCache.setExpire(fromSid, 60 * 5)
    }

    override fun supportsPartialMessages(): Boolean {
        // TODO:?
        return false
    }
}