package org.cufy.mmrpc.gen.kotlin.util.gen

import org.cufy.mmrpc.Marker3
import org.cufy.mmrpc.StructDefinition
import org.cufy.mmrpc.gen.kotlin.GenScope

private const val TAG = "calculateStructStrategy"

enum class StructStrategy {
    DATA_OBJECT,
    DATA_CLASS,
}

@Marker3
fun GenScope.calculateStructStrategy(element: StructDefinition): StructStrategy {
    debugRequireGeneratedClass(TAG, element)

    if (element.structFields.isEmpty())
        return StructStrategy.DATA_OBJECT

    return StructStrategy.DATA_CLASS
}
