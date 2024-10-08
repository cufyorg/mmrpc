/*
 *	Copyright 2024 cufy.org
 *
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *	    http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 */
@file:Suppress("INAPPLICABLE_JVM_NAME")

package org.cufy.mmrpc

import kotlin.jvm.JvmName
import kotlin.reflect.KProperty

////////////////////////////////////////

abstract class NamespaceObject {
    var namespace: Namespace
        protected set

    constructor() {
        this.namespace = Namespace(inferSegment())
    }

    constructor(vararg segments: String) {
        this.namespace = Namespace(*segments)
    }

    constructor(parent: NamespaceObject) {
        this.namespace = parent.namespace + inferSegment()
    }

    constructor(parent: NamespaceObject, vararg segments: String) {
        this.namespace = parent.namespace + segments.asList()
    }

    private fun inferSegment(): String {
        return this::class.simpleName.orEmpty()
    }
}

////////////////////////////////////////

fun interface UnnamedBlock<out T> {
    operator fun invoke(ns: Namespace, name: String?): T
}

class Unnamed<out T>(private val block: UnnamedBlock<T>) {
    constructor(block: (Namespace) -> T) : this({ ns, _ -> block(ns) })
    constructor(value: T) : this({ _, _ -> value })

    fun get(namespace: Namespace) =
        block(namespace, name = null)

    fun get(namespace: Namespace, name: String) =
        block(namespace, name)

    fun get(obj: NamespaceObject) =
        block(obj.namespace, name = null)

    fun get(obj: NamespaceObject, name: String) =
        block(obj.namespace, name)

    private val values = mutableMapOf<Pair<Namespace, String?>, T>()

    operator fun getValue(namespace: Namespace, property: KProperty<*>): T {
        return values.getOrPut(namespace to property.name) {
            val splits = property.name.split("__")
            val ns = namespace + splits.dropLast(1)
            val n = splits.last()
            block(ns, n)
        }
    }

    operator fun getValue(obj: NamespaceObject, property: KProperty<*>): T {
        return values.getOrPut(obj.namespace to property.name) {
            val splits = property.name.split("__")
            val ns = obj.namespace + splits.dropLast(1)
            val n = splits.last()
            block(ns, n)
        }
    }

    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): Unnamed<T> {
        return Unnamed { namespace, name -> block(namespace, name) }
    }
}

////////////////////////////////////////

open class DomainProperty<T> {
    lateinit var value: Unnamed<T>

    operator fun timesAssign(value: T) {
        this.value = Unnamed(value)
    }

    operator fun timesAssign(value: Unnamed<T>) {
        this.value = value
    }
}

open class OptionalDomainProperty<T> {
    var value: Unnamed<T>? = null

    operator fun timesAssign(value: T) {
        this.value = Unnamed(value)
    }

    operator fun timesAssign(value: Unnamed<T>) {
        this.value = value
    }
}

open class NamespaceDomainProperty {
    var value: Namespace = Namespace.Toplevel

    operator fun timesAssign(value: Namespace) {
        this.value = value
    }

    operator fun timesAssign(value: NamespaceObject) {
        this.value = value.namespace
    }
}

open class OptionalLiteralDomainProperty {
    var value: Literal? = null

    operator fun timesAssign(value: Literal) {
        this.value = value
    }

    operator fun timesAssign(value: ConstDefinition) {
        this.value = value.constValue
    }
}

////////////////////////////////////////

@Marker3
interface EndpointDefinitionSetDomainContainer {
    @JvmName("unaryPlusUnnamedEndpointDefinition")
    operator fun Unnamed<EndpointDefinition>.unaryPlus()

    @JvmName("unaryPlusIterableUnnamedEndpointDefinition")
    operator fun Iterable<Unnamed<EndpointDefinition>>.unaryPlus() {
        for (it in this) +it
    }

    @JvmName("unaryPlusEndpointDefinition")
    operator fun EndpointDefinition.unaryPlus() {
        +Unnamed(this)
    }

    @JvmName("unaryPlusIterableEndpointDefinition")
    operator fun Iterable<EndpointDefinition>.unaryPlus() {
        for (it in this) +Unnamed(it)
    }
}

@Marker3
interface FaultDefinitionSetDomainContainer {
    @JvmName("unaryPlusUnnamedFaultDefinition")
    operator fun Unnamed<FaultDefinition>.unaryPlus()

    @JvmName("unaryPlusIterableUnnamedFaultDefinition")
    operator fun Iterable<Unnamed<FaultDefinition>>.unaryPlus() {
        for (it in this) +it
    }

    @JvmName("unaryPlusFaultDefinition")
    operator fun FaultDefinition.unaryPlus() {
        +Unnamed(this)
    }

    @JvmName("unaryPlusIterableFaultDefinition")
    operator fun Iterable<FaultDefinition>.unaryPlus() {
        for (it in this) +Unnamed(it)
    }
}

@Marker3
interface TypeDefinitionSetDomainContainer {
    @JvmName("unaryPlusUnnamedTypeDefinition")
    operator fun Unnamed<TypeDefinition>.unaryPlus()

    @JvmName("unaryPlusIterableUnnamedTypeDefinition")
    operator fun Iterable<Unnamed<TypeDefinition>>.unaryPlus() {
        for (it in this) +it
    }

    @JvmName("unaryPlusTypeDefinition")
    operator fun TypeDefinition.unaryPlus() {
        +Unnamed(this)
    }

    @JvmName("unaryPlusIterableTypeDefinition")
    operator fun Iterable<TypeDefinition>.unaryPlus() {
        for (it in this) +Unnamed(it)
    }
}

@Marker3
interface StructDefinitionSetDomainContainer {
    @JvmName("unaryPlusUnnamedStructDefinition")
    operator fun Unnamed<StructDefinition>.unaryPlus()

    @JvmName("unaryPlusIterableUnnamedStructDefinition")
    operator fun Iterable<Unnamed<StructDefinition>>.unaryPlus() {
        for (it in this) +it
    }

    @JvmName("unaryPlusStructDefinition")
    operator fun StructDefinition.unaryPlus() {
        +Unnamed(this)
    }

    @JvmName("unaryPlusIterableStructDefinition")
    operator fun Iterable<StructDefinition>.unaryPlus() {
        for (it in this) +Unnamed(it)
    }
}

@Marker3
interface ConstDefinitionSetDomainContainer {
    @JvmName("unaryPlusUnnamedConstDefinition")
    operator fun Unnamed<ConstDefinition>.unaryPlus()

    @JvmName("unaryPlusIterableUnnamedConstDefinition")
    operator fun Iterable<Unnamed<ConstDefinition>>.unaryPlus() {
        for (it in this) +it
    }

    @JvmName("unaryPlusConstDefinition")
    operator fun ConstDefinition.unaryPlus() {
        +Unnamed(this)
    }

    @JvmName("unaryPlusIterableConstDefinition")
    operator fun Iterable<ConstDefinition>.unaryPlus() {
        for (it in this) +Unnamed(it)
    }
}

@Marker3
interface RoutineDefinitionSetDomainContainer {
    @JvmName("unaryPlusUnnamedRoutineDefinition")
    operator fun Unnamed<RoutineDefinition>.unaryPlus()

    @JvmName("unaryPlusIterableUnnamedRoutineDefinition")
    operator fun Iterable<Unnamed<RoutineDefinition>>.unaryPlus() {
        for (it in this) +it
    }

    @JvmName("unaryPlusRoutineDefinition")
    operator fun RoutineDefinition.unaryPlus() {
        +Unnamed(this)
    }

    @JvmName("unaryPlusIterableRoutineDefinition")
    operator fun Iterable<RoutineDefinition>.unaryPlus() {
        for (it in this) +Unnamed(it)
    }

    operator fun String.invoke(
        block: RoutineDefinitionBuilder.() -> Unit,
    ) {
        +Unnamed { namespace, _ ->
            RoutineDefinitionBuilder()
                .also { it.name = this }
                .also { it.namespace *= namespace }
                .apply(block)
                .build()
        }
    }
}

@Marker3
interface FieldDefinitionSetDomainContainer {
    @JvmName("unaryPlusUnnamedFieldDefinition")
    operator fun Unnamed<FieldDefinition>.unaryPlus()

    @JvmName("unaryPlusIterableUnnamedFieldDefinition")
    operator fun Iterable<Unnamed<FieldDefinition>>.unaryPlus() {
        for (it in this) +it
    }

    @JvmName("unaryPlusFieldDefinition")
    operator fun FieldDefinition.unaryPlus() {
        +Unnamed(this)
    }

    @JvmName("unaryPlusIterableFieldDefinition")
    operator fun Iterable<FieldDefinition>.unaryPlus() {
        for (it in this) +Unnamed(it)
    }

    operator fun String.invoke(
        type: TypeDefinition,
        block: FieldDefinitionBuilder.() -> Unit = {},
    ) {
        +Unnamed { namespace, _ ->
            FieldDefinitionBuilder()
                .also { it.name = this }
                .also { it.namespace *= namespace }
                .also { it.type *= type }
                .apply(block)
                .build()
        }
    }

    operator fun String.invoke(
        type: Unnamed<TypeDefinition>,
        block: FieldDefinitionBuilder.() -> Unit = {},
    ) {
        +Unnamed { namespace, _ ->
            FieldDefinitionBuilder()
                .also { it.name = this }
                .also { it.namespace *= namespace }
                .also { it.type *= type }
                .apply(block)
                .build()
        }
    }
}

////////////////////////////////////////

@Marker3
abstract class ElementDefinitionBuilder {
    abstract var name: String
    open val namespace = NamespaceDomainProperty()
    open var description = ""

    protected open val metadata = mutableListOf<MetadataDefinitionUsage>()

    open operator fun String.unaryPlus() {
        description += this.trimIndent()
    }

    @JvmName("unaryPlusMetadata")
    operator fun MetadataDefinitionUsage.unaryPlus() {
        metadata += this
    }

    @JvmName("unaryPlusIterableMetadata")
    operator fun Iterable<MetadataDefinitionUsage>.unaryPlus() {
        metadata += this
    }

    @JvmName("unaryPlusMetadataDefinition")
    operator fun MetadataDefinition.unaryPlus() {
        val classThis = this@ElementDefinitionBuilder
        classThis.metadata += MetadataDefinitionUsageBuilder()
            .also { it.definition = this }
            .build()
    }

    abstract fun build(): ElementDefinition
}

////////////////////////////////////////
