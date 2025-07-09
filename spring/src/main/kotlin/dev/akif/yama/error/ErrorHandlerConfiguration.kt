package dev.akif.yama.error

import dev.akif.yama.CannotPatchToNullException
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@AutoConfiguration
open class ErrorHandlerConfiguration {
    @RestControllerAdvice
    class ErrorHandlerAdvice {
        @ExceptionHandler(CannotPatchToNullException::class)
        fun handleCannotPatchToNullException(e: CannotPatchToNullException): ProblemDetail =
            ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message)
    }
}
