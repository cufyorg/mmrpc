package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.ProtocolDefinition
import org.cufy.mmrpc.RoutineDefinition

@Serializable
@SerialName("protocol")
data class CompactProtocolDefinition(
    @SerialName("canonical_name")
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),
    @SerialName("protocol_routines.ref")
    val protocolRoutines: List<CanonicalName> = emptyList(),
) : CompactElementDefinition

fun ProtocolDefinition.toCompact(strip: Boolean = false): CompactProtocolDefinition {
    return CompactProtocolDefinition(
        canonicalName = this.canonicalName,
        description = if (strip) "" else this.description,
        metadata = this.metadata
            .map { it.toCompact(strip) },
        protocolRoutines = this.protocolRoutines
            .map { it.canonicalName },
    )
}

fun CompactProtocolDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?,
): () -> ProtocolDefinition? {
    return it@{
        ProtocolDefinition(
            name = this.name,
            namespace = this.namespace,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
            protocolRoutines = this.protocolRoutines.map {
                val item = onLookup(it) ?: return@it null
                require(item is RoutineDefinition) {
                    "protocol_routines.ref must point to a RoutineDefinition"
                }
                item
            },
        )
    }
}
