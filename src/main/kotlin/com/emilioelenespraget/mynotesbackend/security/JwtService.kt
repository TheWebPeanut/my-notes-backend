package com.emilioelenespraget.mynotesbackend.security

import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import kotlin.io.encoding.Base64

@Service
class JwtService(
    @Value("JWT_SECRET_BASE64") private val jwtSecret: String,
) {
    private val secretKey = Keys.hmacShaKeyFor(Base64.decode(jwtSecret))
}