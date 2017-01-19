package org.pandaframework.shader.metadata

/**
 * @author Ranie Jade Ramiso
 */
internal data class ShaderProgramMetadata(
    val version: String,
    val shaders: List<ShaderMetadata>
)
