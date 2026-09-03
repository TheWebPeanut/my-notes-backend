package com.emilioelenespraget.mynotesbackend.database.repository

import com.emilioelenespraget.mynotesbackend.database.model.Note
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface NoteRepository: MongoRepository<Note, ObjectId> {
    fun findByOwnerId(ownerId: ObjectId): List<Note>
}