@file:Suppress("PackageDirectoryMismatch")

package org.cufy.mmrpc.compact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.cufy.mmrpc.*

@Serializable
@SerialName("kafka_endpoint")
data class CompactKafkaEndpointDefinition(
    @SerialName("canonical_name")
    override val canonicalName: CanonicalName,
    override val description: String = "",
    override val metadata: List<CompactMetadataDefinitionUsage> = emptyList(),
    @SerialName("endpoint_topic")
    val endpointTopic: KafkaTopic,
    @SerialName("endpoint_security_inter")
    val endpointSecurityInter: List<KafkaSecurity> = listOf(
        Kafka.KafkaACL,
    ),
) : CompactElementDefinition

fun KafkaEndpointDefinition.toCompact(strip: Boolean = false): CompactKafkaEndpointDefinition {
    return CompactKafkaEndpointDefinition(
        canonicalName = this.canonicalName,
        description = if (strip) "" else this.description,
        metadata = this.metadata
            .map { it.toCompact(strip) },
        endpointTopic = this.endpointTopic,
        endpointSecurityInter = this.endpointSecurityInter,
    )
}

fun CompactKafkaEndpointDefinition.inflate(
    onLookup: (CanonicalName) -> ElementDefinition?,
): () -> KafkaEndpointDefinition? {
    return it@{
        KafkaEndpointDefinition(
            name = this.name,
            namespace = this.namespace,
            description = this.description,
            metadata = this.metadata.map {
                it.inflate(onLookup)() ?: return@it null
            },
            endpointTopic = this.endpointTopic,
            endpointSecurityInter = this.endpointSecurityInter,
        )
    }
}
