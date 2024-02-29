package com.example.springboot3demo20230810.modules.chat_message

import com.example.springboot3demo20230810.entity.ChatMessage
import com.example.springboot3demo20230810.modules.chat_message.model.ChatMessageSimple
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ChatMessageRepository : JpaRepository<ChatMessage, Long> {

    fun findAllBySidMinAndAndSidMaxOrderByCreatedTimeDesc(
        sidMin: Long,
        sidMax: Long,
        pageable: Pageable
    ): List<ChatMessageSimple>

    fun findAllBySidMinAndAndSidMaxAndIdLessThanOrderByCreatedTimeDesc(
        sidMin: Long,
        sidMax: Long,
        id: Long,
        pageable: Pageable
    ): List<ChatMessageSimple>
}