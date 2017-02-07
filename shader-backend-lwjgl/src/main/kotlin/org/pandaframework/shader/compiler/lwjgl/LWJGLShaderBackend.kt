package org.pandaframework.shader.compiler.lwjgl

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL31
import org.pandaframework.shader.backend.CompileResult
import org.pandaframework.shader.backend.LinkResult
import org.pandaframework.shader.backend.ShaderBackend
import org.pandaframework.shader.stage.ShaderStage

/**
 * @author Ranie Jade Ramiso
 */
class LWJGLShaderBackend: ShaderBackend {
    override fun compileShader(stage: ShaderStage, source: String): CompileResult {
        val shader = GL20.glCreateShader(toShaderType(stage))
        GL20.glShaderSource(shader, source)
        GL20.glCompileShader(shader)

        return if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) != GL11.GL_TRUE) {
            CompileResult.Error(GL20.glGetShaderInfoLog(shader))
        } else {
            CompileResult.Success(shader)
        }
    }

    override fun linkProgram(shaders: List<Int>): LinkResult {
        val program = GL20.glCreateProgram()
        GL20.glUseProgram(program)
        shaders.forEach { GL20.glAttachShader(program, it) }
        GL20.glLinkProgram(program)
        GL20.glUseProgram(0)

        return if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) != GL11.GL_TRUE) {
            LinkResult.Error(GL20.glGetProgramInfoLog(program))
        } else {
            LinkResult.Success(program)
        }
    }

    override fun deleteShader(shader: Int) {
        GL20.glDeleteShader(shader)
    }

    override fun deleteProgram(program: Int) {
        GL20.glDeleteProgram(program)
    }

    override fun useProgram(program: Int) {
        GL20.glUseProgram(program)
    }

    override fun getUniformLocation(program: Int, name: String): Int {
        return GL20.glGetUniformLocation(program, name)
    }

    override fun getUniformBlockIndex(program: Int, name: String): Int {
        return GL31.glGetUniformBlockIndex(program, name)
    }

    override fun setUniformBlockBinding(program: Int, index: Int, binding: Int) {
        GL31.glUniformBlockBinding(program, index, binding)
    }

    private fun toShaderType(stage: ShaderStage): Int {
        return when (stage) {
            ShaderStage.Vertex -> GL20.GL_VERTEX_SHADER
            ShaderStage.Fragment -> GL20.GL_FRAGMENT_SHADER
        }
    }
}
