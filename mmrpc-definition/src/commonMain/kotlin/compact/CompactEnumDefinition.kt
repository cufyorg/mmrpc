package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.*

@Suppress("PropertyName")
@Serializable
@SerialName("enum")
data class CompactEnumDefinition(
    override val canonical_name: CanonicalName,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),

    val type_ref: CanonicalName,
    val entries_ref: List<CanonicalName>,
) : CompactElementDefinition

fun EnumDefinition.toCompact(strip: Boolean = false): CompactEnumDefinition {
    return CompactEnumDefinition(
        canonical_name = this.canonicalName,
        description = if (strip) "" else this.description,
        metadata = this.metadata.map { it.toCompact(strip) },
        type_ref = this.type.canonicalName,
        entries_ref = this.entries.map { it.canonicalName }
    )
}

fun CompactEnumDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?,
): () -> EnumDefinition? {
    return it@{
        EnumDefinition(
            canonicalName = this.canonical_name,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
            type = this.type_ref.let {
                val item = onLookup(it) ?: return@it null
                require(item is TypeDefinition) {
                    "<enum>.type_ref must point to a TypeDefinition"
                }
                item
            },
            entries = this.entries_ref.map {
                val item = onLookup(it) ?: return@it null
                require(item is ConstDefinition) {
                    "<enum>.entries_ref must point to a ConstDefinition"
                }
                item
            },
        )
    }
}
