package com.ecotrade.server.user.controller

import com.ecotrade.server.security.JwtUtil
import com.ecotrade.server.user.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userService: UserService,
    private val jwtUtil: JwtUtil,
    private val passwordEncoder: PasswordEncoder
) {

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<Map<String, String>> {
        val user = userService.findByEmail(loginRequest.email)

        if (user.isEmpty) { // User not found with that email
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("error" to "Invalid Email"))
        }

        if (!passwordEncoder.matches(loginRequest.password, user.get().password)) { // Invalid Password
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("error" to "Invalid Password"))
        }

        val accessToken = jwtUtil.generateAccessToken(user.get().email)
        val refreshToken = jwtUtil.generateRefreshToken(user.get().email)

        return ResponseEntity.ok(
            mapOf("accessToken" to accessToken, "refreshToken" to refreshToken)
        )
    }

    @PostMapping("/refresh-token")
    fun refreshToken(
        @RequestHeader("Authorization") refreshToken: String
    ): ResponseEntity<Map<String, String>> {
        val token = refreshToken.removePrefix("Bearer ")

        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                mapOf("error" to "Invalid Refresh Token. Log in to be done.")
            )
        }

        val userName = jwtUtil.extractUsername(token)
        val newAccessToken = jwtUtil.generateAccessToken(userName)

        return ResponseEntity.ok().body(mapOf("accessToken" to newAccessToken))
    }
}

data class LoginRequest (
    val email: String,
    val password: String
)