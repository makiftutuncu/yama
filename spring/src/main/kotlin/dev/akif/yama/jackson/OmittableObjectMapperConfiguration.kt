package dev.akif.yama.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import dev.akif.yama.Omittable
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean

/**
 * Autoconfiguration class that enables registration of deserializer for [Omittable] type
 */
@AutoConfiguration
@ConditionalOnClass(ObjectMapper::class)
open class OmittableObjectMapperConfiguration {
    /**
     * [Jackson2ObjectMapperBuilderCustomizer] that registers deserializer for [Omittable] type to the [ObjectMapper]s
     */
    @Bean
    open fun objectMapperCustomizer(): Jackson2ObjectMapperBuilderCustomizer =
        Jackson2ObjectMapperBuilderCustomizer { builder ->
            builder.modulesToInstall(
                SimpleModule().apply {
                    addDeserializer(Omittable::class.java, OmittableDeserializer<Any?>())
                }
            )
        }
}
