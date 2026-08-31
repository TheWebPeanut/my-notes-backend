package com.emilioelenespraget.mynotesbackend.database.model.repository

import com.emilioelenespraget.mynotesbackend.database.model.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository: MongoRepository<User, ObjectId> {
    fun findByEmail(email: String): User?
}