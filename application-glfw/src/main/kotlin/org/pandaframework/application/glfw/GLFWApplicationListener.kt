package org.pandaframework.application.glfw

import org.pandaframework.application.ApplicationListener

/**
 * @author Ranie Jade Ramiso
 */
abstract class GLFWApplicationListener: ApplicationListener<GLFWApplicationPeer>() {
    val window by lazy { getPeer().window }

    open fun onKeyType(key: Int, scanCode: Int, action: Int, mods: Int) { }
    open fun onMouseMove(x: Double, y: Double) { }
    open fun onMouseClick(button: Int, action: Int, mods: Int) { }

    open fun frameBufferResize(width: Int, height: Int) { }
}
