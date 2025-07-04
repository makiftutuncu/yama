package dev.akif.yama.openapi

import com.fasterxml.jackson.databind.type.TypeFactory
import dev.akif.yama.PatchData
import io.swagger.v3.core.converter.AnnotatedType
import io.swagger.v3.core.converter.ModelConverter
import io.swagger.v3.core.converter.ModelConverterContext
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.Schema
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.boot.autoconfigure.AutoConfigurationPackages
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.context.annotation.Configuration
import org.springframework.core.type.filter.AssignableTypeFilter
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties

@Configuration
@ConditionalOnClass(OpenAPI::class)
open class OmittableOpenApiConfiguration {
    @Bean
    open fun omittableAwareModelConverter(): ModelConverter =
        object : ModelConverter {
            override fun resolve(type: AnnotatedType, context: ModelConverterContext, chain: Iterator<ModelConverter>): Schema<*>? {
                val javaType = TypeFactory.defaultInstance().constructType(type.type)
                if (!javaType.rawClass.getSimpleName().equals("Omittable") || !javaType.hasGenericTypes()) {
                    return if (chain.hasNext()) chain.next().resolve(type, context, chain) else null
                }
                val innerType = javaType.bindings.getBoundType(0)

                return context.resolve(AnnotatedType(innerType)).apply {
                    nullable = false
                    description = """Omittable field where, value not being present indicates "leave unchanged", null value indicates "set to null" and non-null value indicates "set to value" behavior"""
                    required = emptyList()
                    readOnly = true
                }
            }
        }

    @Bean
    open fun openApiCustomizer(ctx: ApplicationContext): OpenApiCustomizer =
        OpenApiCustomizer { openApi ->
            val patches = findAllPatchClasses(ctx)
            openApi?.components?.schemas?.forEach { name, schema ->
                schema.properties.forEach { (property, propertySchema) ->
                    if (propertySchema.hasOmittableProperty()) {
                        propertySchema.nullable = patches[name]?.hasOmittableNullable(property) ?: false
                        schema?.required?.remove(property)
                    }
                }
            }
        }

    private fun findAllPatchClasses(ctx: ApplicationContext): Map<String, KClass<PatchData<*>>> {
        val scanner = ClassPathScanningCandidateComponentProvider(false)
        scanner.addIncludeFilter(AssignableTypeFilter(PatchData::class.java))
        return AutoConfigurationPackages
            .get(ctx)
            .flatMap {
                scanner.findCandidateComponents(it).mapNotNull { b ->
                    val clazz = Class.forName(b.beanClassName)
                    @Suppress("UNCHECKED_CAST")
                    clazz.kotlin as? KClass<PatchData<*>>
                }
            }
            .associateBy { it.simpleName.orEmpty() }
    }

    private fun <T> Schema<T>.hasOmittableProperty(): Boolean =
        readOnly == true && description?.contains("Omittable") == true

    private fun <T : Any> KClass<T>.hasOmittableNullable(name: String): Boolean {
        if (this.simpleName != "Omittable") return false
        return declaredMemberProperties
            .firstOrNull { it.name == name }
            ?.returnType
            ?.arguments
            ?.firstOrNull()
            ?.type
            ?.isMarkedNullable
            ?: false
    }
}
