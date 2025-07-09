package dev.akif.yama.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import com.fasterxml.jackson.databind.node.MissingNode
import dev.akif.yama.Omittable
import dev.akif.yama.Omittable.Companion.omitted

/**
 * [JsonDeserializer] implementation for [Omittable] which makes sure if the value is [dev.akif.yama.Omitted],
 * it does not appear in the json at all.
 */
class OmittableDeserializer<T>(
    private val valueType: JavaType? = null
) : JsonDeserializer<Omittable<T?>>(),
    ContextualDeserializer {
    private val nullValue = Omittable.Companion.present(null)

    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext
    ): Omittable<T?> {
        val codec = p.codec
        val node = codec.readTree<JsonNode>(p)
        @Suppress("UNCHECKED_CAST")
        return when {
            node is MissingNode -> omitted
            else -> Omittable.Companion.present(codec.treeToValue(node, valueType!!.rawClass) as T?)
        }
    }

    override fun getNullValue(ctxt: DeserializationContext?): Omittable<T?> = nullValue

    override fun getAbsentValue(ctxt: DeserializationContext?): Any? = omitted

    override fun createContextual(
        ctxt: DeserializationContext,
        property: BeanProperty?
    ): JsonDeserializer<*> {
        val wrapperType = property?.type ?: ctxt.contextualType
        val valueType = wrapperType.containedType(0)
        return OmittableDeserializer<T>(valueType)
    }
}
