package com.example.springboot3demo20230810.components.thread_local

import com.example.springboot3demo20230810.model.LocalUser
import com.example.springboot3demo20230810.model.Student
import org.springframework.stereotype.Component


@Component
class LocalUserContext {

    val local = ThreadLocal<LocalUser>()

    fun set(id: Long, student: Student? = null, exception: Exception? = null) {

        local.set(LocalUser(id, student, exception))
    }

    fun get(): LocalUser {
        return local.get() ?: LocalUser(id = 0)
    }

    fun clear() {
        local.remove()
    }
}