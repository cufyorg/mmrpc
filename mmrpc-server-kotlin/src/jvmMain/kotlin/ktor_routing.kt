package org.cufy.mmrpc.server

import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import org.cufy.mmrpc.HttpEndpointInfo
import org.cufy.mmrpc.RoutineObject

@KtorDsl
fun <R : RoutineObject<*, *>> Route.handle(
    routine: R,
    block: suspend RoutingContext.(R) -> Unit,
) {
    val endpoints = routine.__info__.endpoints
        .filterIsInstance<HttpEndpointInfo>()

    require(endpoints.isNotEmpty()) {
        "Routine does not have an Http endpoint"
    }

    for (endpoint in endpoints) for (method in endpoint.method) {
        route(endpoint.path.value, HttpMethod.parse(method.name)) {
            handle { block(this, routine) }
        }
    }
}
