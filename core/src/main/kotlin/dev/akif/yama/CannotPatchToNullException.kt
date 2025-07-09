package dev.akif.yama

import kotlin.reflect.KType

/**
 * Exception to be thrown when a patch object is being constructed but a non-null property is being set to null
 *
 * @param name Name of the property
 * @param type Type of the property
 *
 * @see [PatchData.toMap]
 */
data class CannotPatchToNullException(
    val name: String,
    val type: KType
) : IllegalStateException("Cannot patch '$name: $type' as null")
