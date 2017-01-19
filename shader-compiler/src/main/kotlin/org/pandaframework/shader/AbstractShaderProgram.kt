package org.pandaframework.shader

import org.pandaframework.shader.backend.CompileResult
import org.pandaframework.shader.backend.LinkResult
import org.pandaframework.shader.backend.ShaderCompilerBackend
import java.util.ArrayList

internal abstract class AbstractShaderProgram(private val backend: ShaderCompilerBackend,
                                              private val shaders: Array<Shader>): ShaderProgram {
    protected fun compileProgram(): Int {
        val shaderIds = ArrayList<Int>()

        return with(backend) {
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
                throw org.pandaframework.shader.ShaderException(compileError.error)
            }

            val program = createProgram()
            val linkResult = linkProgram(program, shaderIds)

            if (linkResult is LinkResult.Error) {
                shaderIds.forEach { deleteShader(it) }
                throw org.pandaframework.shader.ShaderException(linkResult.error)
            }

            shaderIds.forEach {
                deleteShader(it)
            }

            return@with program
        }

    }

    override fun use(callback: () -> Unit) {
        with(backend) {
            useProgram(id)
            callback.invoke()
            useProgram(0)
        }
    }

    override fun delete() {
        with(backend) {
            deleteProgram(id)
        }
    }
}
