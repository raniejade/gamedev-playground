package org.pandaframework.shader.compiler

import org.pandaframework.shader.Shader
import org.pandaframework.shader.ShaderSource
import org.pandaframework.shader.ShaderType
import java.io.InputStream
import java.util.HashMap
import java.util.LinkedHashMap

class ShaderProgramBuilder internal constructor() {
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

    fun source(stream: InputStream): ShaderSource {
        return stream.reader(Charsets.UTF_8).use {
            source(it.readText())
        }
    }

    fun source(source: String): ShaderSource = StringShaderSource(source)

    fun classpathSource(path: String): ShaderSource {
        return classpathSource(path, Thread.currentThread().contextClassLoader)
    }

    fun classpathSource(path: String, classLoader: ClassLoader): ShaderSource {
        return source(classLoader.getResourceAsStream(path))
    }
}
