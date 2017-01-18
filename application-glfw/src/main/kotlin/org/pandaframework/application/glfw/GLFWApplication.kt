package org.pandaframework.application.glfw

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.glfw.GLFWVidMode
import org.lwjgl.glfw.GLFWWindowSizeCallback
import org.lwjgl.system.MemoryUtil.NULL
import org.pandaframework.application.Application
import org.pandaframework.application.ApplicationException
import org.pandaframework.application.glfw.backend.Backend
import kotlin.properties.Delegates

/**
 * @author Ranie Jade Ramiso
 */
class GLFWApplication(val backend: Backend): Application() {
    var keyListener = GLFWKeyListener.NO_OP
    var fullscreen = false

    private var window: Long by Delegates.notNull()

    private val monitor: Long by lazy {
        glfwGetPrimaryMonitor()
    }
    private val videoMode: GLFWVidMode by lazy {
        glfwGetVideoMode(monitor)
    }

    private val errorCallback = GLFWErrorCallback.create { window, description ->  }
    private val internalKeyCallback =
        GLFWKeyCallback.create { window: Long, key: Int, scanCode: Int, action: Int, mods: Int ->
            keyListener.handleKeyEvent(window, key, scanCode, action, mods)
        }

    private val resizeCallback = GLFWWindowSizeCallback.create { window, width, height ->
        onResize(width, height)
    }

    override fun setup() {
        if (!glfwInit()) {
            throw ApplicationException("Failed to initialize GLFW.")
        }

        glfwSetErrorCallback(errorCallback)
        backend.setupWindowHints()

        val monitor = if (fullscreen) {
            this.monitor
        } else {
            NULL
        }


        window = glfwCreateWindow(videoMode.width(), videoMode.height(), title, monitor, NULL)

        if (window == NULL) {
            glfwTerminate()
            throw ApplicationException("Failed to create a window.")
        }

        glfwSetWindowSizeCallback(window, resizeCallback)
        glfwSetKeyCallback(window, internalKeyCallback)

        glfwMakeContextCurrent(window)
        backend.setupContext()
    }

    override fun cleanup() {
        errorCallback.free()
        backend.cleanup()
        internalKeyCallback.free()
        resizeCallback.free()
    }

    override fun shouldTerminate() = glfwWindowShouldClose(window)

    override fun pollEvents() {
        glfwPollEvents()
    }

    override fun flush() {
        glfwSwapBuffers(window)
    }

    override fun time() = glfwGetTime()
}
