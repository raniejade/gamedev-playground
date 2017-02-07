package org.pandaframework.shader.backend

import org.pandaframework.shader.stage.ShaderStage

/**
 * @author Ranie Jade Ramiso
 */
interface ShaderBackend {
    fun compileShader(stage: ShaderStage, source: String): CompileResult
    fun linkProgram(shaders: List<Int>): LinkResult
    fun deleteShader(shader: Int)
    fun deleteProgram(program: Int)
    fun useProgram(program: Int)
    fun getUniformLocation(program: Int, name: String): Int
    fun getUniformBlockIndex(program: Int, name: String): Int
    fun setUniformBlockBinding(program: Int, index: Int, binding: Int)
}
