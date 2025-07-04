package dev.akif.yama

import java.util.concurrent.ConcurrentHashMap
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubtypeOf

data class PatchingContext<Source : Any, Patch : PatchData<Patch>>(val source: Source, val data: PatchData<Patch>) {
    companion object {
        private val propertyCache = ConcurrentHashMap<Pair<KClass<*>, String>, KProperty1<*, *>>()
    }

    private val map = data.toMap()
    @Suppress("UNCHECKED_CAST")
    fun <Property> patched(): ReadOnlyProperty<Source?, Property> =
        ReadOnlyProperty { _, prop ->
            if (map.containsKey(prop.name)) {
                map[prop.name] as Property
            } else {
                val klass = source::class as KClass<Source>
                val sourceProp =
                    propertyCache[klass to prop.name]
                        ?.let { it as KProperty1<Source, Property> }
                        ?: run {
                            val newSourceProp =
                                klass
                                    .declaredMemberProperties
                                    .first { it.name == prop.name && it.returnType.isSubtypeOf(prop.returnType) } as KProperty1<Source, Property>
                            propertyCache[klass to prop.name] = newSourceProp
                            newSourceProp
                        }
                sourceProp.get(source)
            }
        }
}
