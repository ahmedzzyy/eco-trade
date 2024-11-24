package com.ecotrade.server.user.dto

data class PublicProfileDTO(
    val id: Long,
    val email: String,
    val profilePicture: String?
)