package org.pandaframework.shader.source

/**
 * @author Ranie Jade Ramiso
 */
internal class ShaderSourceImpl(private val version: String,
                                private val partials: Array<String>,
                                private val source: String): ShaderSource {
    private val fullSource: String by lazy {
        val partialsCombined = if (partials.isEmpty()) {
            ""
        } else {
            partials.reduce { base, next ->
                """
                $base

                $next"""
            }
        }

        """
        #version $version

        /* partials start */
        $partialsCombined
        /* partials end */

        /* source start */
        $source
        /* source end */"""
    }

    override fun contents() = fullSource
}
