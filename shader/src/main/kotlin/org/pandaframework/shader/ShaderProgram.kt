package org.pandaframework.shader

/**
 * @author Ranie Jade Ramiso
 */
interface ShaderProgram {
    val id: Int
    fun use(callback: () -> Unit)
    fun delete()
}
