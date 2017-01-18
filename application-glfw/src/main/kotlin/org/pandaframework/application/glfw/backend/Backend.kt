package org.pandaframework.application.glfw.backend

/**
 * @author Ranie Jade Ramiso
 */
interface Backend {
    fun setupWindowHints()
    fun setupContext()
    fun cleanup()
}
