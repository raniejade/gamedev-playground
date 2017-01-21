package org.pandaframework.shader.stage

import org.pandaframework.shader.ShaderSourceBuilder

/**
 * @author Ranie Jade Ramiso
 */
interface WithVertexShader {
    fun ShaderSourceBuilder.buildVertexShader()
}
