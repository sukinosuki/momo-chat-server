package com.example.springboot3demo20230810.modules.student

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class StudentUnreadCache {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var redisTemplate: RedisTemplate<String, Any?>

//    @Autowired
//    lateinit var redisUtil: RedisUtil

    fun generateStudentUnreadCountKey(sid: Long): String {
        return "student:$sid:unread_count"
    }

    fun increaseUnreadCount(fromSid: Long, toSid: Long) {

        // hash key需要是string类型?
        val ops = redisTemplate.opsForHash<String, Int>()
        val key = generateStudentUnreadCountKey(toSid)

        val increment = ops.increment(key, fromSid.toString(), 1)
        logger.debug("增加未读数成功, num: $increment, from sid: $fromSid, to sid: $toSid")
    }

    fun clearUnreadCount(fromSid: Long, toSid: Long) {
        val ops = redisTemplate.opsForHash<Int, Int>()
        val key = generateStudentUnreadCountKey(fromSid)

        val delete = ops.delete(key, toSid.toString())
        logger.debug("清空未读数成功, num: $delete, from sid: $fromSid, to sid: $toSid")
    }

    fun getAllUnreadCount(toSid: Long): MutableMap<String, Int> {
        val ops = redisTemplate.opsForHash<String, Int>()
        val key = generateStudentUnreadCountKey(toSid)

        return ops.entries(key)
    }
}