package org.pandaframework.shader

fun Shader.uniform() = Uniform(this)

/**
 * @author Ranie Jade Ramiso
 */
class Uniform(shader: Shader): ShaderProperty<Int>({
    shader.backend.getUniformLocation(shader.program, it)
})
