package org.pandaframework.shader.stage

import org.pandaframework.shader.ShaderSourceBuilder

/**
 * @author Ranie Jade Ramiso
 */
interface WithFragmentShader {
    fun ShaderSourceBuilder.buildFragmentShader()
}
