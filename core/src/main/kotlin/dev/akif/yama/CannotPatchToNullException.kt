package dev.akif.yama

import kotlin.reflect.KType

data class CannotPatchToNullException(
    val name: String,
    val type: KType
) : IllegalStateException("Cannot patch '$name: $type' as null")
