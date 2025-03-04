package com.ecotrade.server.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtUtil {
    private val secretKey: SecretKey = generateSecretKey()
    private val accessTokenValidityInMs: Long = 60 * 60 * 1000 // 1 hour
    private val refreshTokenValidityInMs = accessTokenValidityInMs * 24 * 7 // 1 week

    private fun generateSecretKey(): SecretKey {
        val key = System.getenv("JWT_SECRET_KEY")?.toByteArray()
        require(key != null && key.size >= 32) {
            "JWT_SECRET_KEY must be at least 32 bytes long for HMAC-SHA256"
        }
        return Keys.hmacShaKeyFor(key)
    }

    private fun generateToken(username: String, validityInMs: Long): String {
        return Jwts.builder()
            .subject(username)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + validityInMs))
            .signWith(secretKey)
            .compact()
    }

    fun generateAccessToken(username: String): String {
        return generateToken(username, accessTokenValidityInMs)
    }

    fun generateRefreshToken(username: String): String {
        return generateToken(username, refreshTokenValidityInMs)
    }

    private fun getClaims(token: String) =
        Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload

    fun validateToken(token: String): Boolean {
        return try {
            val claims = getClaims(token)
            claims.expiration.after(Date())
        } catch (e: Exception) { false }
    }

    fun extractUsername(token: String): String {
        return getClaims(token).subject
    }
}