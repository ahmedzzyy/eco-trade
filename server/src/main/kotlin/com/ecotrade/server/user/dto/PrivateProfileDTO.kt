package com.ecotrade.server.user.dto

data class PrivateProfileDTO(
    val id: Long,
    val username: String,
    val email: String,
    val location: String?,
    val profilePicture: String?
)