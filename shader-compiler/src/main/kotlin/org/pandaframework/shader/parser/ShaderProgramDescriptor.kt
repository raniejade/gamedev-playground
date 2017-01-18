package org.pandaframework.shader.parser

/**
 * @author Ranie Jade Ramiso
 */
data class ShaderProgramDescriptor(
    val version: String,
    val shaders: List<ShaderDescriptor>
)
