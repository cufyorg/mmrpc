package org.cufy.mmrpc.gradle.kotlin

import org.cufy.mmrpc.gen.kotlin.GenContext
import org.cufy.mmrpc.gen.kotlin.core.*
import org.cufy.mmrpc.gen.kotlin.core.endpoint.HttpGen
import org.cufy.mmrpc.gen.kotlin.core.endpoint.IframeGen
import org.cufy.mmrpc.gen.kotlin.core.endpoint.KafkaGen
import org.cufy.mmrpc.gen.kotlin.core.endpoint.KafkaPublicationGen

fun applyGen(ctx: GenContext) {
    /* =============== core =============== */

    ConstDefinitionGen(ctx).apply()
    EnumDefinitionGen(ctx).apply()
    FaultDefinitionGen(ctx).apply()
    ProtocolDefinitionGen(ctx).apply()
    RoutineDefinitionGen(ctx).apply()
    FieldDefinitionGen(ctx).apply()
    InterDefinitionGen(ctx).apply()
    MetadataDefinitionGen(ctx).apply()
    ScalarDefinitionGen(ctx).apply()
    StructDefinitionGen(ctx).apply()
    TupleDefinitionGen(ctx).apply()
    UnionDefinitionGen(ctx).apply()

    /* =============== core.endpoint =============== */

    HttpGen(ctx).apply()
    IframeGen(ctx).apply()
    KafkaGen(ctx).apply()
    KafkaPublicationGen(ctx).apply()
}
