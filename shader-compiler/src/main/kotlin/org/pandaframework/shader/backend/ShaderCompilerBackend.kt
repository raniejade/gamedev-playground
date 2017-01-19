package org.pandaframework.shader.backend

import org.pandaframework.shader.ShaderSource
import org.pandaframework.shader.ShaderType

/**
 * @author Ranie Jade Ramiso
 */
interface ShaderCompilerBackend {
    fun createProgram(): Int
    fun createShader(type: ShaderType): Int
    fun compileShader(shader: Int, source: ShaderSource): CompileResult
    fun linkProgram(program: Int, shaders: List<Int>): LinkResult
    fun deleteShader(shader: Int)
    fun deleteProgram(program: Int)
    fun useProgram(program: Int)
}
