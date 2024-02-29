package com.example.springboot3demo20230810.modules.student

import jakarta.validation.constraints.NotNull

data class LoginForm(
    @field:NotNull(message = "id不能为空")
    val id: Long
)

