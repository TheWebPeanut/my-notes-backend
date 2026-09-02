package com.emilioelenespraget.mynotesbackend.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date
import kotlin.io.encoding.Base64
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

@Service
class JwtService(
    @Value("\${JWT_SECRET_BASE64}") private val jwtSecret: String,
) {
    private val secretKey = Keys.hmacShaKeyFor(Base64.decode(jwtSecret))
    private val accessTokenValidityMs = 15.minutes
    val refreshTokenValidityMs = 30.days

    private fun generateToken(
        userId: String,
        type: String,
        expiry: Long,
    ): String {
        val now = Date()
        val expiryDate = Date(now.time + expiry)
        return Jwts.builder()
            .subject(userId)
            .claim("type", type)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact()
    }

    fun generateAccessToken(userId: String): String {
        return generateToken(userId, "access", accessTokenValidityMs.inWholeMilliseconds)
    }

    fun generateRefreshToken(userId: String): String {
        return generateToken(userId, "refresh", refreshTokenValidityMs.inWholeMilliseconds)
    }

    fun validateAccessToken(token: String): Boolean {
        val claims = parseAllClaims(token) ?: return false
        val tokenType = claims["type"] as? String ?: return false
        return tokenType == "access"
    }

    fun validateRefreshToken(token: String): Boolean {
        val claims = parseAllClaims(token) ?: return false
        val tokenType = claims["type"] as? String ?: return false
        return tokenType == "refresh"
    }

    fun getUserIdFromJWT(token: String): String? {
        val rawToken = if(token.startsWith("Bearer ")) {
            token.removePrefix("Bearer ")
        } else token
        val claims = parseAllClaims(rawToken) ?: throw IllegalArgumentException("Invalid token.")
        return claims.subject
    }

    private fun parseAllClaims(token: String): Claims? {
        return try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: Exception) {
            null
        }
    }
}