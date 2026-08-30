package com.emilioelenespraget.mynotesbackend.controller

import com.emilioelenespraget.mynotesbackend.database.model.Note
import com.emilioelenespraget.mynotesbackend.database.model.repository.NoteRepository
import org.bson.types.ObjectId
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.time.Clock
import kotlin.time.Instant

@RestController
@RequestMapping("/notes")
class NoteController(
    private val repository: NoteRepository
) {

    data class NoteRequest(
        val id: String?,
        val ownerId: String,
        val title: String,
        val content: String,
        val color: Long,
    )

    data class NoteResponse(
        val id: String,
        val title: String,
        val content: String,
        val color: Long,
        val createdAt: Instant,
    )

    @PostMapping("/updateNote")
    fun save(body: NoteRequest): NoteResponse {
        val note = repository.save(
            Note(
                id = body.id?.let { ObjectId(it) } ?: ObjectId(),
                ownerId = ObjectId(body.ownerId), // TODO(Pending update due to potential security risk)
                title = body.title,
                content = body.content,
                color = body.color,
                createdAt = Clock.System.now(),
            )
        )

        return NoteResponse(
            id = note.id.toHexString(),
            title = note.title,
            content = note.content,
            color = note.color,
            createdAt = note.createdAt,
        )
    }
}