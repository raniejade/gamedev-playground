package org.pandaframework.application.glfw

import org.pandaframework.application.ApplicationListener

/**
 * @author Ranie Jade Ramiso
 */
abstract class GLFWApplicationListener: ApplicationListener<GLFWApplicationPeer>() {
    open fun onKeyType(window: Long, key: Int, scanCode: Int, action: Int, mods: Int) { }
    open fun onMouseMove(window: Long, x: Double, y: Double) { }
    open fun onMouseClick(window: Long, button: Int, action: Int, mods: Int) { }
}
