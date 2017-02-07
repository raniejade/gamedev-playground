package org.pandaframework.shader

import org.pandaframework.shader.backend.CompileResult
import org.pandaframework.shader.backend.LinkResult
import org.pandaframework.shader.backend.ShaderBackend
import org.pandaframework.shader.stage.ShaderStage
import org.pandaframework.shader.stage.WithFragmentShader
import org.pandaframework.shader.stage.WithVertexShader
import java.io.InputStreamReader
import kotlin.properties.Delegates

/**
 * @author Ranie Jade Ramiso
 */
abstract class Shader(val backend: ShaderBackend,
                      open val convention: Convention = Convention)
    : WithVertexShader, WithFragmentShader {

    val program: Int by lazy { compileProgram() }

    override fun ShaderSourceBuilder.buildVertexShader() {
        contents()
    }

    override fun ShaderSourceBuilder.buildFragmentShader() {
        contents()
    }

    abstract val version: ShaderVersion

    fun ShaderSourceBuilder.contents() = contents(className)

    private fun compileProgram(): Int {
        val shaders = mutableListOf<Int>()

        return with(backend) {
            var compileError: CompileResult.Error? = null
            for (stage in collectStages()) {
                val result = compileShader(stage)
                when (result) {
                    is CompileResult.Success -> shaders.add(result.shader)
                    is CompileResult.Error -> {
                        compileError = result
                    }
                }

                if (compileError != null) {
                    break
                }
            }

            if (compileError != null) {
                shaders.forEach { deleteShader(it) }
                throw ShaderException(compileError.error)
            }

            val linkResult = linkProgram(shaders)

            shaders.forEach {
                deleteShader(it)
            }

            when (linkResult) {
                is LinkResult.Success -> linkResult.program
                is LinkResult.Error -> throw ShaderException(linkResult.error)
            }
        }

    }

    fun delete() {
        with(backend) {
            deleteProgram(program)
        }
    }

    private fun collectStages(): List<ShaderStage> {
        return listOf(ShaderStage.Vertex, ShaderStage.Fragment)
    }

    private fun ShaderBackend.compileShader(stage: ShaderStage): CompileResult {
        val builder = ShaderSourceBuilderImpl(stage).apply {
            when (stage) {
                ShaderStage.Vertex -> buildVertexShader()
                ShaderStage.Fragment -> buildFragmentShader()
            }
        }
        val source = builder.build()
        return compileShader(stage, source)
    }

    inner class ShaderSourceBuilderImpl(val stage: ShaderStage): ShaderSourceBuilder {
        private var path: String by Delegates.notNull()

        override fun contents(name: String) {
            this.path = resolvePath(name)
        }

        fun build(): String {
            val body = InputStreamReader(this@Shader.javaClass.getResourceAsStream(path))
                .use(InputStreamReader::readText)
            return """
            #version ${version.version} ${version.profile}

            $body
            """
        }

        private fun resolvePath(name: String) = "$name.${convention.suffix(stage)}"
    }

    private inline val className: String
        get() {
            return this::class.simpleName!!
        }
}

inline fun <reified T: Shader> using(shader: T, block: () -> Unit) {
    with(shader.backend) {
        useProgram(shader.program)
        block.invoke()
        useProgram(0)
    }
}
