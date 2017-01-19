package org.pandaframework.shader.metadata

import org.pandaframework.shader.ShaderType

internal data class ShaderMetadata(
    val path: String,
    val partials: List<String>,
    val type: ShaderType
)
