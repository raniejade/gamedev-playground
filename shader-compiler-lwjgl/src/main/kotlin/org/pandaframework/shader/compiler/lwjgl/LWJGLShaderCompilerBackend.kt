package org.pandaframework.shader.compiler.lwjgl

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.pandaframework.shader.ShaderSource
import org.pandaframework.shader.ShaderType
import org.pandaframework.shader.backend.CompileResult
import org.pandaframework.shader.backend.LinkResult
import org.pandaframework.shader.backend.ShaderCompilerBackend

/**
 * @author Ranie Jade Ramiso
 */
class LWJGLShaderCompilerBackend: ShaderCompilerBackend {
    override fun createProgram() = GL20.glCreateProgram()

    override fun createShader(type: ShaderType): Int {
        return GL20.glCreateShader(
            when (type) {
                ShaderType.VERTEX -> GL20.GL_VERTEX_SHADER
                ShaderType.FRAGMENT -> GL20.GL_FRAGMENT_SHADER
            }
        )
    }

    override fun compileShader(shader: Int, source: ShaderSource): CompileResult {
        GL20.glShaderSource(shader, source.source())
        GL20.glCompileShader(shader)

        return if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) != GL11.GL_TRUE) {
            CompileResult.Error(GL20.glGetShaderInfoLog(shader))
        } else {
            CompileResult.Success()
        }
    }

    override fun linkProgram(program: Int, shaders: List<Int>): LinkResult {
        GL20.glUseProgram(program)
        shaders.forEach { GL20.glAttachShader(program, it) }
        GL20.glLinkProgram(program)

        return if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) != GL11.GL_TRUE) {
            LinkResult.Error(GL20.glGetProgramInfoLog(program))
        } else {
            LinkResult.Success()
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
}
