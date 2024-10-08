package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.InterDefinition
import org.cufy.mmrpc.StructDefinition

@Serializable
@SerialName("inter")
data class CompactInterDefinition(
    @SerialName("canonical_name")
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),
    @SerialName("inter_types.ref")
    val interTypes: List<CanonicalName>,
) : CompactElementDefinition

fun InterDefinition.toCompact(strip: Boolean = false): CompactInterDefinition {
    return CompactInterDefinition(
        canonicalName = this.canonicalName,
        description = if (strip) "" else this.description,
        metadata = this.metadata
            .map { it.toCompact(strip) },
        interTypes = this.interTypes
            .map { it.canonicalName },
    )
}

fun CompactInterDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?,
): () -> InterDefinition? {
    return it@{
        InterDefinition(
            name = this.name,
            namespace = this.namespace,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
            interTypes = this.interTypes.map {
                val item = onLookup(it) ?: return@it null
                require(item is StructDefinition) {
                    "inter_types.ref must point to a StructDefinition"
                }
                item
            },
        )
    }
}
