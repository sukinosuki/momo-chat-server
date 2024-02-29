package com.example.springboot3demo20230810.components.aop

import com.example.springboot3demo20230810.components.thread_local.LocalUserContext
import com.example.springboot3demo20230810.model.AppException
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

// 在controller方法上加上@NeedAuth注解, 如果未授权或者授权失败(是否授权在PreAuthorize filter里处理), 直接抛出错误
@Aspect
@Component
class NeedAuth {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var localUserContext: LocalUserContext

    @Before(value = "@annotation(com.example.springboot3demo20230810.components.annotation.NeedAuth)")
    fun authorize(joinPoint: JoinPoint?) {
//        logger.info("[authorize] joinPoint $joinPoint") // joinPoint execution(R com.example.springboot3demo20230810.modules.student.StudentController.getAuth())
        val localUser = localUserContext.get()

        if (!localUser.isAuthorized()) {
            logger.error("未授权")
            throw localUser.exception ?: AppException.unAuthorizeError()
        }
    }
}