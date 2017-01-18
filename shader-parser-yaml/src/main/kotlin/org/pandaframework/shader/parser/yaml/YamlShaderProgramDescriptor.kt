package org.pandaframework.shader.parser.yaml

/**
 * @author Ranie Jade Ramiso
 */
data class YamlShaderProgramDescriptor(
    val version: String,
    val vertex: YamlShaderDescriptor,
    val fragment: YamlShaderDescriptor
)
