package dev.akif.yama

import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties

/**
 * Marker interface for the types that are meant to contain [Omittable] properties for payloads
 *
 * Example:
 * ```kotlin
 * data class Foo(val bar: Omittable<String>) : PatchData<Foo>
 * ```
 * can represent both `{}` and `{"bar": "something"}` but not `{"bar": null}`
 *
 * @param Data Reference to the self type
 */
interface PatchData<Data : PatchData<Data>> {
    /**
     * Builds a [Map] of properties defined in this class, respecting the semantic of [Omittable] properties
     *
     * @return A map containing the properties and their values
     */
    @Suppress("UNCHECKED_CAST")
    fun toMap(): Map<String, Any?> {
        val data = this as Data
        val klass = this::class as KClass<Data>
        return buildMap {
            klass.declaredMemberProperties.forEach { property ->
                when (val value = property.get(data)) {
                    is Omittable<*> ->
                        value.whenPresent {
                            val type =
                                property.returnType.arguments
                                    .first()
                                    .type
                            if (type?.isMarkedNullable == false && it == null) {
                                throw CannotPatchToNullException(property.name, type)
                            }
                            put(property.name, it)
                        }

                    else -> put(property.name, value)
                }
            }
        }
    }
}
