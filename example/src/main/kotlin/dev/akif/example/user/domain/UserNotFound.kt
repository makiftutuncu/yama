package dev.akif.example.user.domain

data class UserNotFound(
    val id: Long
) : NoSuchElementException("User $id is not found")
