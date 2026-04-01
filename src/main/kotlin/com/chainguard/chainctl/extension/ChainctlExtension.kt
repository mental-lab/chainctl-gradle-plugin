package com.chainguard.chainctl.extension

open class ChainctlExtension {

    companion object {
        const val EXTENSION_NAME = "chainctlVerify"
    }

    /**
     * When true, the build fails if any JAR fails chainctl verification.
     * Default: true
     */
    var failOnUnverified: Boolean = true

    /**
     * Configuration names to skip during verification.
     * Default: empty (all resolvable configurations are checked)
     */
    var skipConfigurations: List<String> = emptyList()

    /**
     * Path to the chainctl binary.
     * Default: "chainctl" (resolved from PATH)
     */
    var chainctlPath: String = "chainctl"
}
