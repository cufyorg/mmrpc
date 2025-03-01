package org.cufy.mmrpc.gen.kotlin.gen

import com.squareup.kotlinpoet.LIST
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.cufy.mmrpc.ArrayDefinition
import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.GenScope
import org.cufy.mmrpc.gen.kotlin.common.*
import org.cufy.mmrpc.gen.kotlin.util.typealiasSpec

class ArrayDefinitionGen(override val ctx: GenContext) : GenScope() {
    override fun apply() {
        for (element in ctx.elements) {
            if (element !is ArrayDefinition) continue
            if (!hasGeneratedClass(element)) continue
            if (element.canonicalName in ctx.ignore) continue

            failBoundary {
                applyCreateTypealias(element)
            }
        }
    }

    private fun applyCreateTypealias(element: ArrayDefinition) {
        /*
        <namespace> {
            <kdoc>
            [ @<metadata> ]
            typealias <name> = List< <class-of-type> >
        }
        */

        injectFile(element.namespace) {
            addTypeAlias(typealiasSpec(asClassName(element), LIST.parameterizedBy(classOf(element.type))) {
                addKdoc(createKDoc(element))
                addAnnotations(createAnnotationSet(element.metadata))
            })
        }
    }
}
