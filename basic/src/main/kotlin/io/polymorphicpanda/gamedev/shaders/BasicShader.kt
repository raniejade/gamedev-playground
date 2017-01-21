package io.polymorphicpanda.gamedev.shaders

import org.pandaframework.shader.Shader
import org.pandaframework.shader.ShaderVersion
import org.pandaframework.shader.compiler.lwjgl.LWJGLShaderBackend

/**
 * @author Ranie Jade Ramiso
 */
class BasicShader: Shader(LWJGLShaderBackend()) {
    override val version = ShaderVersion("330", ShaderVersion.Profile.Core)
}
