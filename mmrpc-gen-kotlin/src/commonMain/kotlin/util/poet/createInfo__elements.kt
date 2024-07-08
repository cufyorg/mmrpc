package org.cufy.mmrpc.gen.kotlin.util.poet

import com.squareup.kotlinpoet.CodeBlock
import org.cufy.mmrpc.*
import org.cufy.mmrpc.gen.kotlin.GenGroup

@Marker3
fun GenGroup.createInfo(element: FaultDefinition): CodeBlock {
    return createCall(
        function = CodeBlock.of("%T", FaultInfo::class),
        "name" to CodeBlock.of("%S", element.name),
        "namespace" to createBoxedNamespace(element.namespace),
        "metadata" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.metadata.map { createInfoUsage(it) }
        ),
    )
}

@Marker3
fun GenGroup.createInfo(element: FieldDefinition): CodeBlock {
    return createCall(
        function = CodeBlock.of("%T", FieldInfo::class),
        "name" to CodeBlock.of("%S", element.name),
        "namespace" to createBoxedNamespace(element.namespace),
        "metadata" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.metadata.map { createInfoUsage(it) }
        ),
        "type" to refOfInfoOrCreateInfo(element.fieldType),
        "default" to element.fieldDefault.let {
            if (it == null) CodeBlock.of("null")
            else refOfInfoOrCreateInfo(it)
        },
    )
}

@Marker3
fun GenGroup.createInfo(element: MetadataDefinition): CodeBlock {
    return createCall(
        function = CodeBlock.of("%T", MetadataInfo::class),
        "name" to CodeBlock.of("%S", element.name),
        "namespace" to createBoxedNamespace(element.namespace),
        "metadata" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.metadata.map { createInfoUsage(it) }
        ),
        "parameters" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.metadataParameters.map { refOfInfoOrCreateInfo(it) }
        ),
    )
}

@Marker3
fun GenGroup.createInfo(element: MetadataParameterDefinition): CodeBlock {
    return createCall(
        function = CodeBlock.of("%T", MetadataParameterInfo::class),
        "name" to CodeBlock.of("%S", element.name),
        "namespace" to createBoxedNamespace(element.namespace),
        "metadata" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.metadata.map { createInfoUsage(it) }
        ),
        "type" to refOfInfoOrCreateInfo(element.parameterType),
        "default" to element.parameterDefault.let {
            if (it == null) CodeBlock.of("null")
            else refOfInfoOrCreateInfo(it)
        },
    )
}

@Marker3
fun GenGroup.createInfo(element: ProtocolDefinition): CodeBlock {
    return createCall(
        function = CodeBlock.of("%T", ProtocolInfo::class),
        "name" to CodeBlock.of("%S", element.name),
        "namespace" to createBoxedNamespace(element.namespace),
        "metadata" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.metadata.map { createInfoUsage(it) }
        ),
        "routines" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.protocolRoutines.map { refOfInfoOrCreateInfo(it) }
        )
    )
}

@Marker3
fun GenGroup.createInfo(element: RoutineDefinition): CodeBlock {
    return createCall(
        function = CodeBlock.of("%T", RoutineInfo::class),
        "name" to CodeBlock.of("%S", element.name),
        "namespace" to createBoxedNamespace(element.namespace),
        "metadata" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.metadata.map { createInfoUsage(it) }
        ),
        "endpoints" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.routineEndpoints.map { refOfInfoOrCreateInfo(it) }
        ),
        "fault" to createCallSingleVararg(
            function = CodeBlock.of("listOf"),
            element.routineFaultUnion.map { refOfInfoOrCreateInfo(it) }
        ),
        "input" to refOfInfoOrCreateInfo(element.routineInput),
        "output" to refOfInfoOrCreateInfo(element.routineOutput),
    )
}
