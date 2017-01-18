package org.pandaframework.shader.parser.yaml

/**
 * @author Ranie Jade Ramiso
 */
data class YamlShaderDescriptor(
    val source: String,
    val partials: List<String> = emptyList()
)
