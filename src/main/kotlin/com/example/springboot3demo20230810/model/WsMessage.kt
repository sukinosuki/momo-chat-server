package com.example.springboot3demo20230810.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonValue

@JsonIgnoreProperties(ignoreUnknown = true)
data class Message(
    var message: String = "",
    var toSid: Long = 0,
    var fromSid: Long = 0,
    var type: MessageActionType = MessageActionType.Normal, // 1:normal | 2:send pong | 3: user online | 4: user offline
    var msgType: MessageContentType = MessageContentType.Text, // text | picture | voice
    var sendKey: String? = "",
)

enum class MessageActionType(@get:JsonValue var code: Int = 1) {
    Normal(1),
    SendPong(2),
    UserOnline(3),
    UserOffline(4),
    ReadAll(5),
    PingPong(0)
}

enum class MessageContentType(@get:JsonValue var code: Int = 1) {
    Text(1),
    Picture(2),
    Voice(3),
    Stamp(4),
}