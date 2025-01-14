package org.cufy.mmrpc.gen.kotlin.core.endpoint

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.asClassName
import org.cufy.mmrpc.IframeEndpointDefinition
import org.cufy.mmrpc.IframeEndpointObject
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.GenScope
import org.cufy.mmrpc.gen.kotlin.util.F_STATIC_PATH
import org.cufy.mmrpc.gen.kotlin.util.gen.common.createOverrideObjectInfoProperty
import org.cufy.mmrpc.gen.kotlin.util.gen.common.createSerialNameAnnotationSet
import org.cufy.mmrpc.gen.kotlin.util.gen.common.createSerializableAnnotationSet
import org.cufy.mmrpc.gen.kotlin.util.gen.common.createStaticInfoProperty
import org.cufy.mmrpc.gen.kotlin.util.gen.hasGeneratedClass
import org.cufy.mmrpc.gen.kotlin.util.gen.structures.createAnnotationSet
import org.cufy.mmrpc.gen.kotlin.util.gen.structures.createKDoc
import org.cufy.mmrpc.gen.kotlin.util.poet.propertySpec

class IframeGen(override val ctx: GenContext) : GenScope() {
    override fun apply() {
        for (element in ctx.elements) {
            if (element !is IframeEndpointDefinition) continue
            if (!hasGeneratedClass(element)) continue
            if (element.canonicalName in ctx.ignore) continue

            failGenBoundary {
                applyCreateDataObject(element)
            }
        }
    }

    private fun applyCreateDataObject(element: IframeEndpointDefinition) {
        val superinterface = IframeEndpointObject::class.asClassName()

        val staticPath = propertySpec(F_STATIC_PATH, STRING) {
            addModifiers(KModifier.CONST)
            initializer("%S", element.endpointPath.value)
        }

        createObject(element) {
            addModifiers(KModifier.DATA)
            addSuperinterface(superinterface)
            addProperty(staticPath)
            addProperty(createStaticInfoProperty(element))
            addProperty(createOverrideObjectInfoProperty(element))

            addKdoc(createKDoc(element))
            addAnnotations(createAnnotationSet(element.metadata))
            addAnnotations(createSerializableAnnotationSet())
            addAnnotations(createSerialNameAnnotationSet(element.canonicalName.value))
        }
    }
}
