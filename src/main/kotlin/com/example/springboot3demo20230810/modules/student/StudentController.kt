package com.example.springboot3demo20230810.modules.student

import com.example.springboot3demo20230810.components.annotation.NeedAuth
import com.example.springboot3demo20230810.components.chat.sessionPool
import com.example.springboot3demo20230810.components.thread_local.LocalUserContext
import com.example.springboot3demo20230810.model.AppException
import com.example.springboot3demo20230810.model.R
import com.example.springboot3demo20230810.model.Student
import com.example.springboot3demo20230810.util.JWTUtil
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.jetbrains.annotations.NotNull
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class StudentController {

    private val logger = LoggerFactory.getLogger(StudentController::class.java)

    @Autowired
    lateinit var jwtUtil: JWTUtil

    @Autowired
    lateinit var studentUnreadCache: StudentUnreadCache

    @Autowired
    lateinit var studentLoginCache: StudentLoginCache

    @Autowired
    lateinit var localUserContext: LocalUserContext

    val studentList: Array<Student> by lazy {
        logger.info("students init")
        // 读取resource下的json文件
        val resource = ClassPathResource("students.min.json")

//        val mapper = ObjectMapper().registerModule(KotlinModule.Builder().build())
        val mapper = jacksonObjectMapper()
//        val mapper = ObjectMapper() // 需要注册kotlin module不然Student属性为默认值

        // 方式一: json mapper 读取input stream
        val studentList = mapper.readValue(resource.inputStream, Array<Student>::class.java)
//        val studentList = mapper.readValue(resource.inputStream, Array<Student>::class.java)
        // 方式二: json mapper 读取input stream buffered reader
//        val studentList = mapper.readValue(resource.inputStream.bufferedReader(), Array<Student>::class.java)
        // 方式三: 读取string(这里的string由input stream读取出来)
//        val str = resource.inputStream.bufferedReader().use { it.readLine() }
//        val studentList = mapper.readValue(str, Array<Student>::class.java)
        studentList
    }

    @GetMapping("/student")
//    fun getStudents(@RequestHeader("Authorization", required = false) authorization: String?): R<List<Student>> {
    fun getStudents(): R<List<Student>> {
        val localUser = localUserContext.get()

        var unreadCountMap = mapOf<String, Int>()
        if (localUser.isAuthorized()) {
            unreadCountMap = studentUnreadCache.getAllUnreadCount(localUser.id)
        }

        val idToTokenMap = studentLoginCache.multiGet(studentList.map { it.Id })

        // TODO: 从缓存遍历对应student的login token是否存在, 存在则表示最近有登录, 不能让其他用户选择该student登录
        val students = studentList.map { v ->
            val copy = v.copy()

            // 是否在线
            sessionPool.get(v.Id.toString())?.let {
                copy.isOnline = true
            }

            // 未读数
            if (unreadCountMap.containsKey(v.Id.toString())) {
                copy.unreadCount = unreadCountMap[v.Id.toString()] ?: 0
            }
            if (idToTokenMap.containsKey(v.Id)) {
                copy.isLatestLogin = true
            }

            copy
        }

        return R.ok(students)
    }

    // TODO: 校验登录的student否是在线，已经是在线的学生不允许让其他用户登录
    // 使用redis缓存当前登录student的token, 如果从redis取到student的token, 则不让其他用户登录该student
    // 客户端断开ws后，设置对应的student的token一个过期时间, 超过该过期时间则需重新登录
    @PostMapping("/auth/login")
    fun login(@Valid @RequestBody @NotNull loginForm: LoginForm, response:HttpServletResponse): R<String> {

        // 判断登录id是否是正确的student id
        val isFound = studentList.any { student -> student.Id == loginForm.id }

        if (!isFound) {
            // TODO: 调用接口随便传id的可以拉黑
            throw AppException.actionFailError("请选择其他学生")
//            response.sendRedirect("https://www.baidu.com")
        }

        // 校验student是否可用, 学生是否在线，学生登录token是否在缓存里
        val (exist) = studentLoginCache.exist(loginForm.id)
        if (exist) {
            throw AppException.actionFailError("当前学生已经登录, 请选择其他学生")
        }

        // 生成token
        val token = jwtUtil.createToken(loginForm.id)

        // 缓存token, 有效时间为5分钟, ws连接成功后会设置为1天
        studentLoginCache.set(loginForm.id, token, 60 * 5)

        return R.ok(token)
    }

    @NeedAuth
    @PostMapping("/auth/logout")
    fun logout(): R<Nothing> {
        val localUser = localUserContext.get()
        logger.info("登出开始, localUser: $localUser")

        studentLoginCache.delete(localUser.id)

        return R.ok()
    }

    @NeedAuth
    @GetMapping("/auth")
    fun getAuth(): R<Student> {
        val localUser = localUserContext.get()

        val student = studentList.first { it.Id == localUser.id }

        return R.ok(student)
    }
}