package dev.akif.example.user.domain

import java.time.LocalDate

data class CreateUserRequest(
    val email: String,
    val name: String,
    val dateOfBirth: LocalDate?
)
