package com.example.springboot3demo20230810.components.runner

import com.example.springboot3demo20230810.modules.student.StudentLoginCache
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class AppInitialLoginTokenCacheRunner : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var loginCache: StudentLoginCache
    override fun run(vararg args: String?) {
        // TODO: 程序启动时, 设置缓存login token的过期时间

        loginCache.setAllExpire(60 * 5)
    }
}

//@Component
//class Runner1 : ApplicationRunner {
//    private val logger = LoggerFactory.getLogger(this::class.java)
//
//    override fun run(args: ApplicationArguments?) {
//        logger.info("args: $args")
//    }
//}
//
//@Component
//class CustomizeInitializingBean : InitializingBean {
//    private val logger = LoggerFactory.getLogger(this::class.java)
//
//    override fun afterPropertiesSet() {
//        logger.info("初始化1")
//    }
//}
//
//@Component
//class CacheRefreshListener : ApplicationListener<ContextRefreshedEvent> {
//    private val logger = LoggerFactory.getLogger(this::class.java)
//
//    override fun onApplicationEvent(event: ContextRefreshedEvent) {
//        logger.info("初始化2")
//    }
//}