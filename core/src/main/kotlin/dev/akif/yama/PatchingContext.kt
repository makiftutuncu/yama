package dev.akif.yama

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubtypeOf

private val propertyCache = ConcurrentHashMap<Pair<KClass<*>, String>, KProperty1<*, *>>()

/**
 * Wrapper for the scope where patching occurs
 *
 * @param Source Type of the object to patch
 * @param Patch Type of the data used in patching
 *
 * @see [PatchData]
 * @see [patchUsing]
 */
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

    /**
     * Convenience method to get a "patched" value of the source object's property
     *
     * @param property Reference to the property of source object
     *
     * @param Source Type of the object to patch
     *
     * @return Value of the property in patch data if it is present or original value of the property of the source object
     */
    @Suppress("UNCHECKED_CAST")
    fun <Property> patched(property: Source.() -> KProperty<Property>): Property =
        with(property(source)) {
            if (map.containsKey(name)) {
                return@with map[name] as Property
            }
            val valueFromMap = propertyCache[klass to name]
            val propertyToUse =
                if (valueFromMap != null) {
                    valueFromMap as KProperty1<Source, Property>
                } else {
                    val sourceProperty = klass.member(this) as KProperty1<Source, Property>
                    propertyCache[klass to name] = sourceProperty
                    sourceProperty
                }
            propertyToUse.get(source)
        }
}
