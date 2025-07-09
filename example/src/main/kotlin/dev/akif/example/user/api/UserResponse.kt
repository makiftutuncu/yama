package dev.akif.example.user.api

import dev.akif.example.user.domain.User
import java.time.LocalDate

data class UserResponse(
    val id: Long,
    val email: String,
    val name: String,
    val dateOfBirth: LocalDate?
) {
    constructor(user: User) : this(
        id = user.id,
        email = user.email,
        name = user.name,
        dateOfBirth = user.dateOfBirth
    )
}
