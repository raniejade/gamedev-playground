package org.pandaframework.shader

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.pandaframework.asset.Asset
import org.pandaframework.asset.AssetDirectory
import org.pandaframework.shader.backend.ShaderCompilerBackend
import org.pandaframework.shader.metadata.ShaderProgramMetadata
import java.io.InputStreamReader
import java.util.HashMap

/**
 * @author Ranie Jade Ramiso
 */
class ShaderCompiler(val backend: ShaderCompilerBackend, val shaderDirectory: AssetDirectory) {
    private val mapper: ObjectMapper by lazy {
        ObjectMapper(YAMLFactory()).apply {
            registerModule(KotlinModule())
        }
    }

    private val partialsDir by lazy {
        shaderDirectory.directory("_partials")
    }

    private val partialsCache = HashMap<String, String>()

    fun compile(name: String, lazy: Boolean = false): ShaderProgram {
        val dir = shaderDirectory.directory(name)
        return dir.asset("_shader.yml").inputStream().use {
            val metadata: ShaderProgramMetadata = mapper.readValue(it)
            val shaders = metadata.shaders.map {
                val partials = it.partials
                    .map { loadPartial(it) }
                    .toTypedArray()

                val source = ShaderSourceImpl(
                    metadata.version,
                    partials,
                    readContents(dir.asset(it.path))
                )

                when (it.type) {
                    ShaderType.VERTEX -> Shader.Vertex(source)
                    ShaderType.FRAGMENT -> Shader.Fragment(source)
                }
            }.toTypedArray()

            if (lazy) {
                LazyShaderProgram(backend, shaders)
            } else {
                EagerShaderProgram(backend, shaders)
            }
        }
    }


    private fun loadPartial(name: String): String {
        return partialsCache.computeIfAbsent(name) {
            readContents(partialsDir.asset(name))
        }
    }

    private fun readContents(shader: Asset): String {
        return InputStreamReader(shader.inputStream())
            .use(InputStreamReader::readText)
    }
}
