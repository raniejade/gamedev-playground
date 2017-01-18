package org.pandaframework.application.glfw

/**
 * @author Ranie Jade Ramiso
 */
interface GLFWKeyListener {
    fun handleKeyEvent(window: Long, key: Int, scanCode: Int, action: Int, mods: Int)

    companion object {
        val NO_OP = object: GLFWKeyListener {
            override fun handleKeyEvent(window: Long, key: Int, scanCode: Int, action: Int, mods: Int) { }
        }
    }
}
