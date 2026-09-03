package com.emilioelenespraget.mynotesbackend.database.model

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import kotlin.time.Clock
import kotlin.time.Instant

@Document("refresh_tokens")
data class RefreshToken(
    val userId: ObjectId,
    @Indexed(expireAfter = "0s")
    val expiresAt: Instant,
    val hashedToken: String,
    val createdAt: Instant = Clock.System.now(),
)
