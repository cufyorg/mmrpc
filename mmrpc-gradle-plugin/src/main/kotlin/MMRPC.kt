package org.cufy.mmrpc.gradle

object MMRPC {
    const val VERSION = "0.0.1-experimental"
    const val GROUP_NAME = "mmrpc"
    const val EXTENSION_NAME = "mmrpc"

    object Defaults {
        val DIRECTORIES = setOf(
            "src/main/resources/",
            "src/commonMain/resources/",
        )
    }
}
