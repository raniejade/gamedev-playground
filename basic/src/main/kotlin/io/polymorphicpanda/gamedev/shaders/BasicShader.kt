package io.polymorphicpanda.gamedev.shaders

import org.pandaframework.shader.Shader
import org.pandaframework.shader.ShaderVersion
import org.pandaframework.shader.compiler.lwjgl.LWJGLShaderBackend
import org.pandaframework.shader.uniform

/**
 * @author Ranie Jade Ramiso
 */
class BasicShader: Shader(LWJGLShaderBackend()) {
    val ourColor by uniform()
    val modelMatrix by uniform()
    val viewMatrix by uniform()
    val projectionMatrix by uniform()

    override val version = ShaderVersion("330", ShaderVersion.Profile.Core)
}
