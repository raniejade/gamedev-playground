package org.pandaframework.shader.loader.yaml.parser

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.pandaframework.shader.ShaderType
import org.pandaframework.shader.loader.parser.ShaderDescriptor
import org.pandaframework.shader.loader.parser.ShaderProgramDescriptor
import org.pandaframework.shader.loader.parser.ShaderProgramParser
import java.io.InputStream

/**
 * @author Ranie Jade Ramiso
 */
class YamlShaderProgramParser: ShaderProgramParser {
    private val mapper: ObjectMapper by lazy {
        ObjectMapper(YAMLFactory()).apply {
            registerModule(KotlinModule())
        }
    }

    override fun parse(source: InputStream): ShaderProgramDescriptor {
        var descriptor = mapper.readValue(source, YamlShaderProgramDescriptor::class.java)

        val vertexShaderDescriptor = descriptor.vertex
        val fragmentShaderDescriptor = descriptor.fragment
        return ShaderProgramDescriptor(
            descriptor.version,
            shaders = arrayListOf(
                ShaderDescriptor(vertexShaderDescriptor.source, vertexShaderDescriptor.partials, ShaderType.VERTEX),
                ShaderDescriptor(fragmentShaderDescriptor.source, fragmentShaderDescriptor.partials, ShaderType.FRAGMENT)
            )
        )
    }
}
