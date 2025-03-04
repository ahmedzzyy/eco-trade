package com.ecotrade.server.security

import org.springframework.security.authentication.AbstractAuthenticationToken

class JwtAuthentication(private val userName:String): AbstractAuthenticationToken(null) {
    override fun getCredentials(): Any? { return null }

    override fun getPrincipal(): Any { return userName }

    override fun isAuthenticated(): Boolean { return true }
}