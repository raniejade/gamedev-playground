package org.pandaframework.shader

import org.pandaframework.shader.backend.ShaderCompilerBackend

internal class LazyShaderProgram(backend: ShaderCompilerBackend, shaders: Array<Shader>)
    : AbstractShaderProgram(backend, shaders) {
    override val id: Int by lazy { compileProgram() }
}
