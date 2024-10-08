package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.ScalarDefinition

@Serializable
@SerialName("scalar")
data class CompactScalarDefinition(
    @SerialName("canonical_name")
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),
) : CompactElementDefinition

fun ScalarDefinition.toCompact(strip: Boolean = false): CompactScalarDefinition {
    return CompactScalarDefinition(
        canonicalName = this.canonicalName,
        description = if (strip) "" else this.description,
        metadata = this.metadata
            .map { it.toCompact(strip) },
    )
}

fun CompactScalarDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?,
): () -> ScalarDefinition? {
    return it@{
        ScalarDefinition(
            name = this.name,
            namespace = this.namespace,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
        )
    }
}
