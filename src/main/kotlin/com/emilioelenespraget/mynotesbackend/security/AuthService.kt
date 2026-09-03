package com.emilioelenespraget.mynotesbackend.security

import com.emilioelenespraget.mynotesbackend.database.model.RefreshToken
import com.emilioelenespraget.mynotesbackend.database.model.User
import com.emilioelenespraget.mynotesbackend.database.repository.RefreshTokenRepository
import com.emilioelenespraget.mynotesbackend.database.repository.UserRepository
import org.bson.types.ObjectId
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64

@Service
class AuthService(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val hashEncoder: HashEncoder,
    private val refreshTokenRepository: RefreshTokenRepository,
    ) {
    data class TokenPair(
        val accessToken: String,
        val refreshToken: String,
    )

    fun register(email: String, password: String): User {
        return userRepository.save(
            User(
                email = email,
                hashedPassword = hashEncoder.encode(password)
                    ?: throw NullPointerException("Invalid input."),
            )
        )
    }

    fun login(email: String, password: String): TokenPair {
        val user = userRepository.findByEmail(email)
            ?: throw BadCredentialsException("Invalid credentials.")

        if(!hashEncoder.matches(password, user.hashedPassword)) {
            throw BadCredentialsException("Invalid credentials.")
        }

        val newAccessToken = jwtService.generateAccessToken(user.id.toHexString())
        val newRefreshToken = jwtService.generateRefreshToken(user.id.toHexString())

        return TokenPair(
            newAccessToken,
            newRefreshToken,
        )
    }

    private fun storeRefreshToken(userId: ObjectId, rawRefreshToken: String) {
        val hashed = hashToken(rawRefreshToken)
        val expiryMs = jwtService.refreshTokenValidityMs
        val expiresAt = Instant.now().plusMillis(expiryMs.inWholeMilliseconds)

        refreshTokenRepository.save(
            RefreshToken(
                userId,
                TODO(),
                hashed
            )
        )
    }

    private fun hashToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }

}