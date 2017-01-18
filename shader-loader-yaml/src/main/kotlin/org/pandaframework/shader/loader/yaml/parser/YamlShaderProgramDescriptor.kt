package org.pandaframework.shader.loader.yaml.parser

/**
 * @author Ranie Jade Ramiso
 */
data class YamlShaderProgramDescriptor(
    val version: String,
    val vertex: YamlShaderDescriptor,
    val fragment: YamlShaderDescriptor
)
