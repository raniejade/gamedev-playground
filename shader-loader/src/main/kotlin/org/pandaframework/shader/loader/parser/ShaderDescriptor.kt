package org.pandaframework.shader.loader.parser

import org.pandaframework.shader.ShaderType


data class ShaderDescriptor(
    val source: CharSequence,
    val partials: List<String>,
    val type: ShaderType
)
