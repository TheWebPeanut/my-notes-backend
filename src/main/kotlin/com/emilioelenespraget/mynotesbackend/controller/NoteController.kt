package com.emilioelenespraget.mynotesbackend.controller

import com.emilioelenespraget.mynotesbackend.database.model.Note
import com.emilioelenespraget.mynotesbackend.repository.NoteRepository
import org.bson.types.ObjectId
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import kotlin.time.Clock
import kotlin.time.Instant

@RestController
@RequestMapping("/notes")
class NoteController(private val repository: NoteRepository) {

    data class NoteRequest(
        val id: String?,
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
    fun save(
        @RequestBody body: NoteRequest
    ): NoteResponse {
        val note = repository.save(
            Note(
                id = body.id?.let { ObjectId(it) } ?: ObjectId.get(),
                ownerId = ObjectId(), // TODO(Pending update due to potential security risk)
                title = body.title,
                content = body.content,
                color = body.color,
                createdAt = Clock.System.now(),
            )
        )

        return note.toResponse()
    }

    @GetMapping("/getNotes")
    fun findByOwnerId(
        @RequestParam(required = true) ownerId: String,
    ): List<NoteResponse> {
        return repository.findByOwnerId(ObjectId(ownerId)).map {
            it.toResponse()
        }
    }

    @DeleteMapping(path = ["/{id}"])
    fun deleteById(@PathVariable id: String) {
        repository.deleteById(ObjectId(id))
    }
}

private fun Note.toResponse(): NoteController.NoteResponse {
    return NoteController.NoteResponse(
        id = id.toHexString(),
        title = title,
        content = content,
        color = color,
        createdAt = createdAt,
    )
}