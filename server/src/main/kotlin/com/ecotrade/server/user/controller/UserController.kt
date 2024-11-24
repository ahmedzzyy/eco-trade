package com.ecotrade.server.user.controller

import com.ecotrade.server.user.dto.PrivateProfileDTO
import com.ecotrade.server.user.dto.PublicProfileDTO
import com.ecotrade.server.user.service.UserService
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

    @GetMapping("/{id}")
    fun getCurrentUser(@PathVariable id: Long): ResponseEntity<PrivateProfileDTO> {
        val currentUserEmail = SecurityContextHolder.getContext().authentication.principal as String

        val user = userService.getUserById(id)

        if (user.email != currentUserEmail) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).build<PrivateProfileDTO>()
        }

        val privateUserDTO = PrivateProfileDTO(
            user.id,
            user.email,
            user.location,
            user.profilePicture,
        )

        return ResponseEntity.ok(privateUserDTO)
    }

    @GetMapping("/{id}/public")
    fun getPublicUser(@PathVariable id: Long): ResponseEntity<PublicProfileDTO> {
        val user = userService.getUserById(id)

        val publicUserDTO = PublicProfileDTO(
            user.id,
            user.email,
            user.profilePicture
        )

        return ResponseEntity.ok(publicUserDTO)
    }
}