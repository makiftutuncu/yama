package dev.akif.example.user.domain

import dev.akif.example.user.data.UserEntity
import java.time.Instant
import java.time.LocalDate

data class User(
    val id: Long,
    val email: String,
    val name: String,
    val dateOfBirth: LocalDate?,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    constructor(user: UserEntity) : this(
        id = requireNotNull(user.id),
        name = user.name,
        email = user.email,
        dateOfBirth = user.dateOfBirth,
        createdAt = user.createdAt,
        updatedAt = user.updatedAt
    )
}
