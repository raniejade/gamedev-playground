package org.pandaframework.shader.compiler

import org.pandaframework.shader.ShaderSource

/**
 * @author Ranie Jade Ramiso
 */
internal class StringShaderSource(private val source: String): ShaderSource {
    override fun source() = source
}
