package com.example.springboot3demo20230810

import com.example.springboot3demo20230810.modules.student.StudentLoginCache
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SpringBoot3Demo20230810ApplicationTests {

    val logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var loginCache: StudentLoginCache

    @Test
    fun contextLoads() {

        val keys = loginCache.getAllKeys()
        keys.forEach {
            logger.info("key: $it")
        }

        logger.info("keys: $keys, size: ${keys.size}")
    }

}
