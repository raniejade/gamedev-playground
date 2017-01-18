package org.pandaframework.shader

/**
 * @author Ranie Jade Ramiso
 */
sealed class Shader(val source: ShaderSource, val type: ShaderType) {
    class Vertex(source: ShaderSource): Shader(source, ShaderType.VERTEX)
    class Fragment(source: ShaderSource): Shader(source, ShaderType.FRAGMENT)
}
