package org.pandaframework.shader.parser.yaml

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.pandaframework.shader.ShaderType
import org.pandaframework.shader.parser.ShaderDescriptor
import org.pandaframework.shader.parser.ShaderProgramDescriptor
import org.pandaframework.shader.parser.ShaderProgramParser
import java.io.InputStream

/**
 * @author Ranie Jade Ramiso
 */
class YamlShaderProgramParser: ShaderProgramParser() {
    private val mapper: ObjectMapper by lazy {
        ObjectMapper(YAMLFactory()).apply {
            registerModule(KotlinModule())
        }
    }

    override fun doParse(inputStream: InputStream): ShaderProgramDescriptor {
        val descriptor = mapper.readValue(inputStream, YamlShaderProgramDescriptor::class.java)

        val vertex = descriptor.vertex
        val fragment = descriptor.fragment
        return ShaderProgramDescriptor(
            descriptor.version,
            shaders = arrayListOf(
                ShaderDescriptor(vertex.source, vertex.partials, ShaderType.VERTEX),
                ShaderDescriptor(fragment.source, fragment.partials, ShaderType.FRAGMENT)
            )
        )
    }
}
