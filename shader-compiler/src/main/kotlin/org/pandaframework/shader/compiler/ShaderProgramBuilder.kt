package org.pandaframework.shader.compiler

import org.pandaframework.asset.AssetManager
import org.pandaframework.shader.Shader
import org.pandaframework.shader.ShaderSource
import org.pandaframework.shader.ShaderType
import org.pandaframework.shader.parser.ShaderProgramParser
import java.io.InputStream
import java.util.HashMap
import java.util.LinkedHashMap

class ShaderProgramBuilder internal constructor(private val assetManager: AssetManager,
                                                private val parser: ShaderProgramParser) {
    internal var lazy = false
    internal val shaders: HashMap<ShaderType, Shader> = LinkedHashMap()

    fun lazy() {
        lazy = true
    }

    fun attach(shader: Shader) {
        shaders.put(shader.type, shader)
    }

    fun vertexShader(source: ShaderSource) = Shader.Vertex(source)

    fun fragmentShader(source: ShaderSource) = Shader.Fragment(source)

    fun source(path: String): ShaderSource {
        val inputStream = assetManager.load(path).inputStream()
        return inputStream.reader(Charsets.UTF_8).use {
            raw(it.readText())
        }
    }

    fun raw(source: String): ShaderSource = StringShaderSource(source)

    fun from(path: String) {
        parser.parse(asset(path)).forEach {
            attach(when (it.key) {
                ShaderType.VERTEX -> vertexShader(raw(it.value))
                ShaderType.FRAGMENT -> fragmentShader(raw(it.value))
            })
        }
    }

    private fun asset(path: String): InputStream {
        return assetManager.load(path).inputStream()
    }
}
