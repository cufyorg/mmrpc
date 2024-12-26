package org.cufy.mmrpc.gradle.kotlin

import org.cufy.mmrpc.CanonicalName
import org.cufy.mmrpc.ElementDefinition
import org.cufy.mmrpc.ProtocolDefinition
import org.cufy.mmrpc.RoutineDefinition
import org.cufy.mmrpc.gen.kotlin.GenRange

fun collectIgnored(
    elements: Set<ElementDefinition>,
    includeRange: GenRange,
): Set<CanonicalName> {
    if (includeRange == GenRange.EVERYTHING) return emptySet()

    val commRoots = elements.asSequence()
        .filter { it is ProtocolDefinition || it is RoutineDefinition }
        .map { it.canonicalName.asNamespace }
        .toSet()

    if (includeRange == GenRange.SHARED_ONLY) {
        return elements.asSequence()
            .map { it.canonicalName.asNamespace }
            .filter { elementCN ->
                commRoots.any { rootCN ->
                    elementCN == rootCN || elementCN in rootCN
                }
            }
            .map { it.canonicalName }
            .toSet()
    }

    if (includeRange == GenRange.COMM_ONLY) {
        return elements.asSequence()
            .map { it.canonicalName.asNamespace }
            .filter { elementCN ->
                commRoots.none { rootCN ->
                    elementCN == rootCN || elementCN in rootCN
                }
            }
            .map { it.canonicalName }
            .toSet()
    }

    return emptySet()
}
