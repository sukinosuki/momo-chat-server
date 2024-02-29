package com.example.springboot3demo20230810.model

data class LocalUser(
    var id: Long,
    var user: Student? = null,
    var exception: Exception? = null
) {
    fun isAuthorized(): Boolean {

        return id.toInt() != 0
    }
}
