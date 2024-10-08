/*
 *	Copyright 2024 cufy.org
 *
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *	    http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 */
package org.cufy.mmrpc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

////////////////////////////////////////

@Serializable
@SerialName("array")
data class ArrayDefinition(
    override val name: String = ANONYMOUS_NAME,
    override val namespace: Namespace = Namespace.Toplevel,
    override val description: String = "",
    override val metadata: List<MetadataDefinitionUsage> = emptyList(),
    @SerialName("array_type")
    val arrayType: TypeDefinition,
) : TypeDefinition() {
    companion object {
        const val ANONYMOUS_NAME = "(anonymous[])"
    }

    override fun collectChildren() = sequence {
        yieldAll(metadata.asSequence().flatMap { it.collect() })
        yieldAll(arrayType.collect())
    }
}

open class ArrayDefinitionBuilder :
    ElementDefinitionBuilder() {
    override var name = ArrayDefinition.ANONYMOUS_NAME

    open val type = DomainProperty<TypeDefinition>()

    override fun build(): ArrayDefinition {
        val asNamespace = this.namespace.value + this.name
        return ArrayDefinition(
            name = this.name,
            namespace = this.namespace.value,
            description = this.description,
            metadata = this.metadata.toList(),
            arrayType = this.type.value.get(asNamespace, name = "type"),
        )
    }
}

@Marker2
internal fun array(
    block: ArrayDefinitionBuilder.() -> Unit = {},
): Unnamed<ArrayDefinition> {
    return Unnamed { namespace, name ->
        ArrayDefinitionBuilder()
            .also { it.name = name ?: return@also }
            .also { it.namespace *= namespace }
            .apply(block)
            .build()
    }
}

////////////////////////////////////////

@Marker2
fun array(
    type: TypeDefinition,
    block: ArrayDefinitionBuilder.() -> Unit = {},
): Unnamed<ArrayDefinition> {
    return array { this.type *= type; block() }
}

@Marker2
fun array(
    type: Unnamed<TypeDefinition>,
    block: ArrayDefinitionBuilder.() -> Unit = {},
): Unnamed<ArrayDefinition> {
    return array { this.type *= type; block() }
}

////////////////////////////////////////

@Marker2
val TypeDefinition.array: Unnamed<ArrayDefinition>
    get() = array(this)

@Marker2
val Unnamed<TypeDefinition>.array: Unnamed<ArrayDefinition>
    get() = array(this)

////////////////////////////////////////
