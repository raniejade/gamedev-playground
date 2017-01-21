package org.pandaframework.shader

import org.pandaframework.shader.stage.ShaderStage

/**
 * @author Ranie Jade Ramiso
 */
interface Convention {
    fun suffix(stage: ShaderStage): String


    companion object DefaultConvention: Convention {
        override fun suffix(stage: ShaderStage): String {
            return when (stage) {
                ShaderStage.Vertex -> "vert"
                ShaderStage.Fragment -> "frag"
            }
        }

    }
}
