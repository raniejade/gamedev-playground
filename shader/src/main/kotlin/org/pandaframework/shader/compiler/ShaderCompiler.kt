package org.pandaframework.shader.compiler

import org.pandaframework.shader.Shader
import org.pandaframework.shader.ShaderProgram
import org.pandaframework.shader.ShaderSource
import org.pandaframework.shader.ShaderType
import java.util.ArrayList

/**
 * @author Ranie Jade Ramiso
 */
abstract class ShaderCompiler {
    fun createProgram(callback: ShaderProgramBuilder.() -> Unit): ShaderProgram {
        val builder = ShaderProgramBuilder()
        callback.invoke(builder)

        return if (builder.lazy) {
            LazyShaderProgram(builder.shaders.values.toTypedArray())
        } else {
            EagerShaderProgram(builder.shaders.values.toTypedArray())
        }
    }


    protected abstract fun createProgram(): Int
    protected abstract fun createShader(type: ShaderType): Int
    protected abstract fun compileShader(shader: Int, source: ShaderSource): CompileResult
    protected abstract fun linkProgram(program: Int, shaders: List<Int>): LinkResult
    protected abstract fun deleteShader(shader: Int)
    protected abstract fun deleteProgram(program: Int)
    protected abstract fun useProgram(program: Int)


    inner abstract class AbstractShaderProgram(private val shaders: Array<Shader>): ShaderProgram {
        protected fun compileProgram(): Int {
            val shaderIds = ArrayList<Int>()

            var compileError: CompileResult.Error? = null
            for (shader in shaders) {
                val id = createShader(shader.type)

                val result = compileShader(id, shader.source)
                if (result is CompileResult.Error) {
                    compileError = result
                    break
                }

                shaderIds.add(id)
            }

            if (compileError != null) {
                shaderIds.forEach { deleteShader(it) }
                throw ShaderException(compileError.error)
            }

            val program = createProgram()
            val linkResult = linkProgram(program, shaderIds)

            if (linkResult is LinkResult.Error) {
                shaderIds.forEach { deleteShader(it) }
                throw ShaderException(linkResult.error)
            }

            shaderIds.forEach {
                deleteShader(it)
            }

            return program
        }

        override fun use(callback: () -> Unit) {
            useProgram(id)
            callback.invoke()
            useProgram(0)
        }

        override fun delete() {
            deleteProgram(id)
        }
    }

    inner class LazyShaderProgram(shaders: Array<Shader>): AbstractShaderProgram(shaders) {
        override val id: Int by lazy { compileProgram() }
    }

    inner class EagerShaderProgram(shaders: Array<Shader>): AbstractShaderProgram(shaders) {
        override val id: Int = compileProgram()
    }
}
