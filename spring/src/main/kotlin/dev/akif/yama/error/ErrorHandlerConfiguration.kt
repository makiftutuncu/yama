package dev.akif.yama.error

import dev.akif.yama.CannotPatchToNullException
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Autoconfiguration class to enable error handling rest controller advices
 */
@AutoConfiguration
open class ErrorHandlerConfiguration {
    /**
     * [RestControllerAdvice] that can handle yama exceptions
     */
    @RestControllerAdvice
    class ErrorHandlerAdvice {
        /**
         * [ExceptionHandler] that handles [CannotPatchToNullException]s as 400 Bad Request responses
         */
        @ExceptionHandler(CannotPatchToNullException::class)
        fun handleCannotPatchToNullException(e: CannotPatchToNullException): ProblemDetail =
            ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message)
    }
}
