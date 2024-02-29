package com.example.springboot3demo20230810.entity

import com.example.springboot3demo20230810.model.MessageActionType
import com.example.springboot3demo20230810.model.MessageContentType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(
    indexes = [
        Index(name = "idx_sidmin_sidmax", columnList = "sid_min, sid_max")
    ]
)
data class ChatMessage(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0,

    @Column(name = "from_sid", nullable = false)
    var fromSid: Long = 0,

    @Column(name = "to_sid", nullable = false)
    var toSid: Long = 0,

    @Column(name = "sid_min", nullable = false)
    var sidMin: Long = 0,

    @Column(name = "sid_max", nullable = false)
    var sidMax: Long = 0,

    @Column(nullable = false)
    var message: String = "",

    @Column(name = "msg_type", nullable = false)
    var msgType: MessageContentType = MessageContentType.Text,

    @Column(nullable = false)
    var type: MessageActionType = MessageActionType.Normal,

    @CreatedDate
    @Column(name = "created_time", nullable = false)
    var createdTime: LocalDateTime? = null,

    @LastModifiedDate
    @Column(name = "updated_time", nullable = false)
    var updatedTime: LocalDateTime? = null
)
