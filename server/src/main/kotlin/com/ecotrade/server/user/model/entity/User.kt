package com.ecotrade.server.user.model.entity

import jakarta.persistence.*
import jakarta.validation.constraints.Email

@Entity
@Table(name = "users")
data class User (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    @Email
    val email: String,

    @Column(nullable = false)
    val password: String,

    @Column(nullable = false)
    val username: String,

    val location: String? = null,

    val profilePicture: String? = null
)