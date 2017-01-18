package org.pandaframework.shader.parser

import org.pandaframework.shader.ShaderType
import java.io.InputStream
import java.util.HashMap

/**
 * @author Ranie Jade Ramiso
 */
abstract class ShaderProgramParser {
    fun parse(inputStream: InputStream): Map<ShaderType, String> {
        val result = HashMap<ShaderType, String>()
        val descriptor = doParse(inputStream)

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

    protected abstract fun doParse(inputStream: InputStream): ShaderProgramDescriptor
}
