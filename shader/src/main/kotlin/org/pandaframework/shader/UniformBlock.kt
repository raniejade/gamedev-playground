package org.pandaframework.shader

fun Shader.uniformBlock(binding: Int) = UniformBlock(this, binding)

/**
 * @author Ranie Jade Ramiso
 */
class UniformBlock(shader: Shader, binding: Int): ShaderProperty<Int>({
    with(shader.backend) {
        val index = getUniformBlockIndex(shader.program, it)
        setUniformBlockBinding(shader.program, index, binding)
    }

    binding
})
