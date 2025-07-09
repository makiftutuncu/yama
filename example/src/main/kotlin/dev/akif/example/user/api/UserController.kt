package dev.akif.example.user.api

import dev.akif.example.user.domain.CreateUserRequest
import dev.akif.example.user.domain.PatchUserRequest
import dev.akif.example.user.domain.UpdateUserRequest
import dev.akif.example.user.domain.UserNotFound
import dev.akif.example.user.domain.UserService
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedModel
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/v1/users")
class UserController(
    private val users: UserService
) {
    @PostMapping
    fun createUser(
        @RequestBody request: CreateUserRequest
    ): ResponseEntity<UserResponse> {
        val user = users.create(request.email, request.name, request.dateOfBirth)
        return ResponseEntity.created(userUri(user.id)).body(UserResponse(user))
    }

    @GetMapping
    fun listUsers(request: Pageable): PagedModel<UserResponse> {
        val page = users.list(request).map { UserResponse(it) }
        return PagedModel(page)
    }

    @GetMapping("/{id:\\d+}")
    fun getUser(
        @PathVariable id: Long
    ): UserResponse {
        val user = users.get(id) ?: throw UserNotFound(id)
        return UserResponse(user)
    }

    @PutMapping("/{id:\\d+}")
    fun updateUser(
        @PathVariable id: Long,
        @RequestBody request: UpdateUserRequest
    ): UserResponse = UserResponse(users.update(id, request))

    @PatchMapping("/{id:\\d+}")
    fun patchUser(
        @PathVariable id: Long,
        @RequestBody request: PatchUserRequest
    ): UserResponse = UserResponse(users.patch(id, request))

    @DeleteMapping("/{id:\\d+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUser(
        @PathVariable id: Long
    ) {
        users.delete(id)
    }

    @ExceptionHandler(UserNotFound::class)
    fun handleNotFound(e: UserNotFound): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message).apply {
            type = usersUri
            instance = userUri(e.id)
        }

    private val usersUri: URI = URI.create("/v1/users")

    private fun userUri(id: Long): URI = URI.create("/v1/users/$id")
}
