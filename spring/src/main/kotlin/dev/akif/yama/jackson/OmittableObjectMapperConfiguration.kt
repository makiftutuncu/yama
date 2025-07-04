package dev.akif.yama.jackson

import com.fasterxml.jackson.databind.module.SimpleModule
import dev.akif.yama.Omittable
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class OmittableObjectMapperConfiguration {
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
