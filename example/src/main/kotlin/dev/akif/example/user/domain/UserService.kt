package dev.akif.example.user.domain

import dev.akif.example.user.data.UserEntity
import dev.akif.example.user.data.UserRepository
import dev.akif.yama.patchUsing
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDate
import kotlin.jvm.optionals.getOrNull

@Service
class UserService(
    private val users: UserRepository
) {
    @Transactional
    fun create(
        email: String,
        name: String,
        dateOfBirth: LocalDate?
    ): User =
        User(
            users.save(
                UserEntity(
                    email = email,
                    name = name,
                    dateOfBirth = dateOfBirth
                )
            )
        )

    fun list(request: Pageable): Page<User> = users.findAll(request).map { User(it) }

    fun get(id: Long): User? = users.findById(id).map { User(it) }.getOrNull()

    @Transactional
    fun update(
        id: Long,
        request: UpdateUserRequest
    ): User {
        val user = users.findById(id).orElseThrow { UserNotFound(id) }
        val updated = users.save(user.copy(name = request.name, dateOfBirth = request.dateOfBirth, updatedAt = Instant.now()))
        return User(updated)
    }

    @Transactional
    fun patch(
        id: Long,
        request: PatchUserRequest
    ): User {
        val user = users.findById(id).orElseThrow { UserNotFound(id) }
        val patched =
            user.patchUsing(request) {
                val name = patched { ::name }
                val dateOfBirth = patched { ::dateOfBirth }
                it.copy(name = name, dateOfBirth = dateOfBirth)
            }
        val updated = patched.takeIf { it != user }?.let { users.save(it.copy(updatedAt = Instant.now())) } ?: user
        return User(updated)
    }

    @Transactional
    fun delete(id: Long) {
        val affected = users.delete(id)
        if (affected != 1) {
            throw UserNotFound(id)
        }
    }
}
