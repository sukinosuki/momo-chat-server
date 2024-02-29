package com.example.springboot3demo20230810.util

import com.example.springboot3demo20230810.properties.JWTProperty
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class JWTUtil {

    @Autowired
    lateinit var jwtProperty: JWTProperty

    private val key by lazy {
        val encodedKey = Base64.getEncoder().encodeToString(jwtProperty.secret.toByteArray())

        Keys.hmacShaKeyFor(encodedKey.toByteArray())
    }

    fun createToken(id: Long): String {

        val expiration = Date(System.currentTimeMillis() + jwtProperty.expiration)
        val sub = id.toString()
        val issueAt = Date()

        return Jwts.builder()
            .signWith(key)
//            .setClaims(map)
            .setSubject(sub)
            .setIssuedAt(issueAt)
            .setExpiration(expiration)
            .compact()
    }

    fun parseToken(token: String): Pair<Claims?, Exception?> {
        return try {
            val claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body

            Pair(claims, null)
        } catch (e: Exception) {
            Pair(null, e)
        }
    }
}