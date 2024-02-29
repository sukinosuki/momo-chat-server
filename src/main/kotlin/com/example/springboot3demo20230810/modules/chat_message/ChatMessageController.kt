package com.example.springboot3demo20230810.modules.chat_message

import com.example.springboot3demo20230810.model.R
import com.example.springboot3demo20230810.modules.chat_message.model.ChatMessageSimple
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController("ChatMessageController")
@RequestMapping("/api/v1")
class ChatMessageController {

    @Autowired
    lateinit var chatMessageRepository: ChatMessageRepository

    @GetMapping("/chat-message")
    fun getAll(
        @RequestParam("sid_min") minSid: Long,
        @RequestParam("sid_max") maxSid: Long,
        @RequestParam("size", required = false) size: Int?,
        @RequestParam("id", required = false) id: Long?
    ): R<List<ChatMessageSimple>> {
        // TODO: 需要校验当前student id与sid_min或者sid_max一致才可以获取该student的聊天记录

        var safeSize = size ?: 50
        if (safeSize < 0) {
            safeSize = 50
        } else if (safeSize > 1000) {
            safeSize = 1000
        }

        var _minSid = minSid
        var _maxSid = maxSid
        if (minSid > maxSid) {
//            val temp = minSid;
            _minSid = maxSid
            _maxSid = minSid
        }
        val page = PageRequest.of(0, safeSize)
        val messages: List<ChatMessageSimple>?
        // 根据message id获取小于该id的数据
        if (id != null) {
            messages = chatMessageRepository.findAllBySidMinAndAndSidMaxAndIdLessThanOrderByCreatedTimeDesc(
                _minSid,
                _maxSid,
                id,
                page
            )
        } else {
            messages = chatMessageRepository
                .findAllBySidMinAndAndSidMaxOrderByCreatedTimeDesc(_minSid, _maxSid, page)
        }

        return R.ok(messages.reversed())
    }
}