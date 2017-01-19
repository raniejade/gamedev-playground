package org.pandaframework.shader

import org.pandaframework.shader.backend.ShaderCompilerBackend

internal class EagerShaderProgram(backend: ShaderCompilerBackend, shaders: Array<Shader>)
    : AbstractShaderProgram(backend, shaders) {
    override val id: Int = compileProgram()
}
