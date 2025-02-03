package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.*

@Suppress("PropertyName")
@Serializable
@SerialName("routine")
data class CompactRoutineDefinition(
    override val canonical_name: CanonicalName,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),

    val comm: List<Comm> = emptyList(),
    val faults_ref: List<CanonicalName> = emptyList(),
    val input_ref: CanonicalName,
    val output_ref: CanonicalName,
) : CompactElementDefinition

fun RoutineDefinition.toCompact(strip: Boolean = false): CompactRoutineDefinition {
    return CompactRoutineDefinition(
        canonical_name = this.canonicalName,
        description = if (strip) "" else this.description,
        metadata = this.metadata.map { it.toCompact(strip) },
        comm = this.comm,
        faults_ref = this.faults.map { it.canonicalName },
        input_ref = this.input.canonicalName,
        output_ref = this.output.canonicalName,
    )
}

fun CompactRoutineDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?,
): () -> RoutineDefinition? {
    return it@{
        RoutineDefinition(
            canonicalName = this.canonical_name,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
            comm = this.comm,
            faults = this.faults_ref.map {
                val item = onLookup(it) ?: return@it null
                require(item is FaultDefinition) {
                    "<routine>.fault_ref must point to a FaultDefinition"
                }
                item
            },
            input = this.input_ref.let {
                val item = onLookup(it) ?: return@it null
                require(item is StructDefinition) {
                    "<routine>.input_ref must point to a StructDefinition"
                }
                item
            },
            output = this.output_ref.let {
                val item = onLookup(it) ?: return@it null
                require(item is StructDefinition) {
                    "<routine>.output_ref must point to a StructDefinition"
                }
                item
            },
        )
    }
}
