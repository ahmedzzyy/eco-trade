package com.ecotrade.server.user.service

import com.ecotrade.server.user.model.User
import com.ecotrade.server.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun registerUser(user: User): User {
        if (userRepository.findByEmail(user.email).isPresent) {
            throw IllegalArgumentException("Email already in use.")
        }

        val password = passwordEncoder.encode(user.password)
        val newUser = User(
            email = user.email,
            username = user.username,
            password = password,
            location = user.location,
            profilePicture = user.profilePicture
        )

        return userRepository.save(newUser)
    }

    fun getUserById(id: Long): User {
        return userRepository.findById(id)
            .orElseThrow {
                IllegalArgumentException("User Not Found")
            }
    }

    fun findByEmail(email: String): Optional<User> {
        return userRepository.findByEmail(email)
    }
}