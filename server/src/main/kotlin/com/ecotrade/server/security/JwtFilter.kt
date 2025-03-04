package com.ecotrade.server.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter(private val jwtUtil: JwtUtil) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val requestURI = request.requestURI

            // Skip JWT filter for public endpoints
            if (requestURI.startsWith("/api/auth/")) {
                filterChain.doFilter(request, response)
                return
            }

            val token = getTokenFromRequest(request)

            if (token == null) {
                sendJSONErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Missing Authentication Token")
                return
            }

            if (jwtUtil.validateToken(token)) {
                val userName = jwtUtil.extractUsername(token)

                val authentication = JwtAuthentication(userName)
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

                SecurityContextHolder.getContext().authentication = authentication
            } else {
                sendJSONErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token")
                return
            }
        } catch (e: io.jsonwebtoken.ExpiredJwtException) {
            sendJSONErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token Expired")
            return
        } catch (e: io.jsonwebtoken.JwtException) {
            sendJSONErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token")
            return
        } catch (e: Exception) {
            sendJSONErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication Failed.")
            return
        }

        filterChain.doFilter(request, response)
    }

    fun getTokenFromRequest(request: HttpServletRequest): String? {
        val authorizationHeader = request.getHeader("Authorization")

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7)
        }

        return null
    }

    fun sendJSONErrorResponse(response: HttpServletResponse, status: Int, message: String) {
        response.contentType = "application/json"
        response.status = status
        response.writer.write("""{"error": "$message"}""")
        response.writer.flush()
    }
}