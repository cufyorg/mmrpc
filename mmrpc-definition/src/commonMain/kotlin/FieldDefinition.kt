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
@SerialName("field")
data class FieldDefinition(
    override val name: String = ANONYMOUS_NAME,
    override val namespace: Namespace = Namespace.Toplevel,
    override val description: String = "",
    override val metadata: List<MetadataDefinitionUsage> = emptyList(),
    @SerialName("field_type")
    val fieldType: TypeDefinition,
    @SerialName("field_default")
    val fieldDefault: Literal? = null,
) : ElementDefinition() {
    companion object {
        const val ANONYMOUS_NAME = "(anonymous<field>)"
    }

    override fun collectChildren() = sequence {
        yieldAll(metadata.asSequence().flatMap { it.collect() })
        yieldAll(fieldType.collect())
    }
}

open class FieldDefinitionBuilder :
    ElementDefinitionBuilder() {
    override var name = FieldDefinition.ANONYMOUS_NAME

    open val type = DomainProperty<TypeDefinition>()
    open val default = OptionalLiteralDomainProperty()

    override fun build(): FieldDefinition {
        val asNamespace = this.namespace.value + this.name
        return FieldDefinition(
            name = this.name,
            namespace = this.namespace.value,
            description = this.description,
            metadata = this.metadata.toList(),
            fieldType = this.type.value.get(asNamespace, name = "type"),
            fieldDefault = this.default.value,
        )
    }
}

@Marker2
internal fun prop(
    block: FieldDefinitionBuilder.() -> Unit = {},
): Unnamed<FieldDefinition> {
    return Unnamed { namespace, name ->
        FieldDefinitionBuilder()
            .also { it.name = name ?: return@also }
            .also { it.namespace *= namespace }
            .apply(block)
            .build()
    }
}

////////////////////////////////////////

@Marker2
fun prop(
    type: TypeDefinition,
    block: FieldDefinitionBuilder.() -> Unit = {},
): Unnamed<FieldDefinition> {
    return prop { this.type *= type; block() }
}

@Marker2
fun prop(
    type: Unnamed<TypeDefinition>,
    block: FieldDefinitionBuilder.() -> Unit = {},
): Unnamed<FieldDefinition> {
    return prop { this.type *= type; block() }
}

////////////////////////////////////////
