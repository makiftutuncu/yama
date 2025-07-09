package dev.akif.example.user.domain

import java.time.LocalDate

data class UpdateUserRequest(
    val name: String,
    val dateOfBirth: LocalDate?
)
