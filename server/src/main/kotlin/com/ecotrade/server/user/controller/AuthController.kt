package com.ecotrade.server.user.controller

import com.ecotrade.server.security.JwtUtil
import com.ecotrade.server.user.model.User
import com.ecotrade.server.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
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

    @Operation(summary = "Register a new user")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "User registered successfully"),
            ApiResponse(responseCode = "400", description = "Invalid user data")
        ]
    )
    @PostMapping("/register")
    fun registerUser(@RequestBody @Valid user: User): ResponseEntity<Void> {
        val registeredUser = userService.registerUser(user)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "Authenticate user and return JWT tokens")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Login successful", content = [
                Content(mediaType = "application/json")
            ]),
            ApiResponse(responseCode = "401", description = "Invalid credentials", content = [Content()])
        ]
    )
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

    @Operation(
        summary = "Refresh the access token using a refresh token",
        security = [SecurityRequirement(name = "BearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "New access token generated", content = [
                Content(mediaType = "application/json")
            ]),
            ApiResponse(responseCode = "401", description = "Invalid refresh token", content = [Content()])
        ]
    )
    @PostMapping("/refresh-token")
    fun refreshToken(
        @Parameter(description = "Refresh token for generating a new access token")
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