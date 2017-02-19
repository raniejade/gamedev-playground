package org.pandaframework.shader

fun Shader.uniform(structure: String = "") = Uniform(this, structure)

/**
 * @author Ranie Jade Ramiso
 */
class Uniform(shader: Shader, val structure: String): ShaderProperty<Int>({
    val property = if (structure.isNotBlank()) {
        "$structure.$it"
    } else {
        it
    }

    shader.backend.getUniformLocation(shader.program, property)
})
