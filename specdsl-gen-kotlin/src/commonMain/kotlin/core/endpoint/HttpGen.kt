package org.cufy.specdsl.gen.kotlin.core.endpoint

import com.squareup.kotlinpoet.*
import org.cufy.specdsl.HttpEndpointDefinition
import org.cufy.specdsl.HttpEndpointInfo
import org.cufy.specdsl.HttpEndpointObject
import org.cufy.specdsl.gen.kotlin.GenContext
import org.cufy.specdsl.gen.kotlin.GenGroup
import org.cufy.specdsl.gen.kotlin.util.asClassName
import org.cufy.specdsl.gen.kotlin.util.fStaticInfo
import org.cufy.specdsl.gen.kotlin.util.fStaticPath
import org.cufy.specdsl.gen.kotlin.util.poet.*

class HttpGen(override val ctx: GenContext) : GenGroup() {
    override fun apply() {
        for (element in ctx.specSheet.collectChildren()) {
            if (element !is HttpEndpointDefinition) continue
            if (element.isAnonymous) continue

            failGenBoundary {
                onObject(element.namespace) {
                    addType(createDataObject(element))
                }
            }
        }
    }

    private fun createDataObject(element: HttpEndpointDefinition): TypeSpec {
        return TypeSpec
            .objectBuilder(element.asClassName)
            .addModifiers(KModifier.DATA)
            .addProperty(createStaticInfoProperty(element))
            .addProperty(createStaticPathProperty(element))
            .overrideObject(element)
            .addKdoc(createKDoc(element))
            .addAnnotations(createAnnotationSet(element.metadata))
            .addAnnotations(createOptionalSerializableAnnotationSet())
            .addAnnotations(createOptionalSerialNameAnnotationSet(element.canonicalName.value))
            .build()
    }

    private fun TypeSpec.Builder.overrideObject(element: HttpEndpointDefinition): TypeSpec.Builder {
        val overrideObjectClass = HttpEndpointObject::class.asClassName()

        val infoPropertySpec = PropertySpec
            .builder("info", HttpEndpointInfo::class)
            .addModifiers(KModifier.OVERRIDE)
            .initializer("%L", element.fStaticInfo)
            .build()

        return this
            .superclass(overrideObjectClass)
            .addProperty(infoPropertySpec)
    }

    private fun createStaticInfoProperty(element: HttpEndpointDefinition): PropertySpec {
        return PropertySpec
            .builder(element.fStaticInfo, HttpEndpointInfo::class)
            .initializer("\n%L", createInfo(element))
            .build()
    }

    private fun createStaticPathProperty(element: HttpEndpointDefinition): PropertySpec {
        return PropertySpec
            .builder(element.fStaticPath, STRING)
            .addModifiers(KModifier.CONST)
            .initializer("%S", element.endpointPath.value)
            .build()
    }
}
