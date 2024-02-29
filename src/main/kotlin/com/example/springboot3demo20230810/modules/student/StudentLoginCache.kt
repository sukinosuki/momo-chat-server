package com.example.springboot3demo20230810.modules.student

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.concurrent.TimeUnit

@Service
class StudentLoginCache {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var stringRedisTemplate: StringRedisTemplate
//    lateinit var redisTemplate: RedisTemplate<String, String>

    fun generateKey(sid: Long): String {
        return "student:$sid:login_token"
    }

    fun setAllExpire(timeout: Long) {
        val keys = stringRedisTemplate.keys("student:**login_token")
        keys.forEach { key ->
            stringRedisTemplate.expire(key, Duration.ofSeconds(timeout))
        }
    }

    fun multiGet(ids: List<Long>): MutableMap<Long, String?> {
        val keys = ids.map { id -> generateKey(id) }
        val values = stringRedisTemplate.opsForValue().multiGet(keys) ?: listOf()

        val idToValueMap = mutableMapOf<Long, String?>()

        ids.forEachIndexed { i, id ->
            val value = values.getOrNull(i)?.let {
                idToValueMap.put(id, it)

            }
//            if (value != null) {
//                idToValueMap.put(id, value)
//            }
        }

        return idToValueMap
    }

    // set
    fun set(sid: Long, token: String, timeout: Long) {
        val ops = stringRedisTemplate.opsForValue()
        val key = generateKey(sid)

        ops.set(key, token, timeout, TimeUnit.SECONDS)
    }

    // get
    fun get(sid: Long): String? {
        val ops = stringRedisTemplate.opsForValue()
        val key = generateKey(sid)
        val token = ops.get(key)

        return token
    }

    fun exist(sid: Long): Pair<Boolean, String?> {
        val ops = stringRedisTemplate.opsForValue()
        val key = generateKey(sid)

        val token = ops.get(key)

        return Pair(token != null, token)
    }

    // 设置过期时间
    fun setExpire(sid: Long, timeout: Long) {
        val ops = stringRedisTemplate.opsForValue()
        val key = generateKey(sid)

//        ops.getAndExpire(key, timeout, TimeUnit.SECONDS)
        stringRedisTemplate.expire(key, Duration.ofSeconds(timeout))
        // redis 6.2才支持? getAndExpire
//        ops.getAndExpire(key, Duration.ofSeconds(timeout))
    }

    // 删除
    fun delete(sid: Long) {
//        val ops = stringRedisTemplate.opsForValue()
        val key = generateKey(sid)


//        ops.getAndDelete(key)
        stringRedisTemplate.delete(key)
    }
}