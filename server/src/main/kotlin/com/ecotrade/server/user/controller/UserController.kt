package com.ecotrade.server.user.controller

import com.ecotrade.server.user.dto.PrivateProfileDTO
import com.ecotrade.server.user.dto.PublicProfileDTO
import com.ecotrade.server.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @Operation(
        summary = "Get the current logged-in user's profile",
        security = [SecurityRequirement(name = "BearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "User profile retrieved successfully", content = [
                Content(mediaType = "application/json", schema = Schema(implementation = PrivateProfileDTO::class))
            ]),
            ApiResponse(responseCode = "401", description = "Unauthorized - User must be logged in", content = [Content()])
        ]
    )
    @GetMapping("/me")
    fun getCurrentUser(): ResponseEntity<PrivateProfileDTO> {
        val authentication = SecurityContextHolder.getContext().authentication

        if (authentication == null || !authentication.isAuthenticated || authentication.principal !is String) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null)
        }

        val currentUserEmail = authentication.principal as String
        val user = userService.findByEmail(currentUserEmail)
            .orElseThrow { RuntimeException("User not found! Login.") }

        val privateUserDTO = PrivateProfileDTO(
            user.id,
            user.username,
            user.email,
            user.location,
            user.profilePicture,
        )

        return ResponseEntity.ok(privateUserDTO)
    }

    @Operation(
        summary = "Get a public profile by user ID",
        security = [SecurityRequirement(name = "BearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Public profile retrieved successfully", content = [
                Content(mediaType = "application/json", schema = Schema(implementation = PublicProfileDTO::class))
            ]),
            ApiResponse(responseCode = "404", description = "User not found", content = [Content()])
        ]
    )
    @GetMapping("/{id}/public")
    fun getPublicUser(@PathVariable id: Long): ResponseEntity<PublicProfileDTO> {
        try {
            val user = userService.getUserById(id)

            val publicUserDTO = PublicProfileDTO(
                user.id,
                user.email,
                user.username,
                user.profilePicture
            )

            return ResponseEntity.ok(publicUserDTO)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        }
    }
}