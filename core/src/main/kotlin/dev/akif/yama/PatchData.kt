package dev.akif.yama

import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties

interface PatchData<Data : PatchData<Data>> {
    @Suppress("UNCHECKED_CAST")
    fun toMap(): Map<String, Any?> {
        val data = this as Data
        val klass = this::class as KClass<Data>
        return buildMap {
            klass.declaredMemberProperties.forEach { property ->
                when (val value = property.get(data)) {
                    is Omittable<*> -> value.whenPresent {
                        val type = property.returnType.arguments.first().type
                        require(type?.isMarkedNullable == true || it != null) {
                            "${property.name} is of type $type but is assigned to null"
                        }
                        put(property.name, it)
                    }
                    else -> put(property.name, value)
                }
            }
        }
    }
}
