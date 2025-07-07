package dev.akif.yama

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubtypeOf

private val propertyCache = ConcurrentHashMap<Pair<KClass<*>, String>, KProperty1<*, *>>()

class PatchingContext<Source : Any, Patch : PatchData<Patch>>(
    private val source: Source,
    data: PatchData<Patch>
) {
    private val klass = source::class
    private val map = data.toMap()

    @Suppress("UNCHECKED_CAST")
    private fun <Source : Any, Property> KClass<Source>.member(property: KProperty<Property>): KProperty1<Source, Property> =
        declaredMemberProperties
            .first { it.name == property.name && it.returnType.isSubtypeOf(property.returnType) }
                as KProperty1<Source, Property>

    @Suppress("UNCHECKED_CAST")
    fun <Property> patched(property: Source.() -> KProperty<Property>): Property =
        with (property(source)) {
            if (map.containsKey(name)) {
                map[name] as Property
            } else {
                val valueFromMap = propertyCache[klass to name]
                if (valueFromMap != null) {
                    valueFromMap as Property
                } else {
                    val sourceProperty = klass.member(this) as KProperty1<Source, Property>
                    propertyCache[klass to name] = sourceProperty
                    sourceProperty.get(source)
                }
            }
        }
}
