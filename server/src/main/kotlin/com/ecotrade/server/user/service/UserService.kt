package com.ecotrade.server.user.service

import com.ecotrade.server.user.model.entity.User
import com.ecotrade.server.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {

    fun registerUser(user: User): User {
        if (userRepository.findByEmail(user.email).isPresent) {
            throw IllegalArgumentException("Email already in use.")
        }

        return userRepository.save(user)
    }

    fun getUserById(id: Long): User {
        return userRepository.findById(id)
            .orElseThrow({
                IllegalArgumentException("User Not Found")
            })
    }
}