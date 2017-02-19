package io.polymorphicpanda.gamedev.shader

import org.pandaframework.shader.Shader
import org.pandaframework.shader.ShaderVersion
import org.pandaframework.shader.compiler.lwjgl.LWJGLShaderBackend
import org.pandaframework.shader.uniform
import org.pandaframework.shader.uniformBlock

/**
 * @author Ranie Jade Ramiso
 */
class PBRShader: Shader(LWJGLShaderBackend()) {
    val model by uniform()

    val albedo by uniform()
    val metallic by uniform()
    val roughness by uniform()
    val ao by uniform()

    val constants by uniformBlock(0)

    override val version: ShaderVersion
        get() = ShaderVersion("330", ShaderVersion.Profile.Core)

}
