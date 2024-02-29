package com.example.springboot3demo20230810.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "jwt")
data class JWTProperty @ConstructorBinding constructor(
    val secret: String,

    val expiration: Long,
)
