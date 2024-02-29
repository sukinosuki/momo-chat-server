package com.example.springboot3demo20230810.modules.chat_message.model

import com.example.springboot3demo20230810.model.MessageActionType
import com.example.springboot3demo20230810.model.MessageContentType
import java.time.LocalDateTime

data class ChatMessageSimple(
    var id: Long = 0,

    var createdTime: LocalDateTime? = null,

    var fromSid: Long = 0,

    var toSid: Long = 0,

    var message: String = "",

    var type: MessageActionType = MessageActionType.Normal,

    var msgType: MessageContentType = MessageContentType.Text,
)
