package org.pandaframework.shader.loader

import org.pandaframework.shader.ShaderProgram
import org.pandaframework.shader.ShaderType
import org.pandaframework.shader.compiler.ShaderCompiler
import org.pandaframework.shader.loader.parser.ShaderProgramParser
import java.io.InputStream
import java.util.HashMap
import java.util.ServiceLoader

/**
 * @author Ranie Jade Ramiso
 */
private object ShaderProgramLoader {
    val parser: ShaderProgramParser by lazy {
        val iterator = ServiceLoader.load(ShaderProgramParser::class.java)
            .iterator()

        iterator.next()!!
    }

    fun load(source: InputStream): Map<ShaderType, String> {
        val result = HashMap<ShaderType, String>()
        val descriptor = parser.parse(source)

        descriptor.shaders.forEach {
            // TODO: partial handling

            val body = """
            #version ${descriptor.version}

            ${it.source}
            """.trimMargin()

            result.put(it.type, body)
        }

        return result
    }
}

fun ShaderCompiler.loadProgram(path: String, lazy: Boolean = false): ShaderProgram {
    return loadProgram(path, lazy, Thread.currentThread().contextClassLoader)
}

fun ShaderCompiler.loadProgram(path: String, lazy: Boolean = false, classLoader: ClassLoader): ShaderProgram {
    return createProgram {
        if (lazy) {
            lazy()
        }

        ShaderProgramLoader.load(classpathResource(path, classLoader)).forEach {
            attach(when (it.key) {
                ShaderType.VERTEX -> vertexShader(source(it.value))
                ShaderType.FRAGMENT -> fragmentShader(source(it.value))
            })
        }
    }
}
