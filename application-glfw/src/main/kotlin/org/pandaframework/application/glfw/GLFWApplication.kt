package org.pandaframework.application.glfw

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWCursorPosCallback
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.glfw.GLFWMouseButtonCallback
import org.lwjgl.glfw.GLFWVidMode
import org.lwjgl.glfw.GLFWWindowSizeCallback
import org.lwjgl.system.MemoryUtil.NULL
import org.pandaframework.application.Application
import org.pandaframework.application.ApplicationException
import org.pandaframework.application.ApplicationPeer
import org.pandaframework.application.glfw.backend.Backend
import kotlin.properties.Delegates

/**
 * @author Ranie Jade Ramiso
 */
class GLFWApplication(val backend: Backend): Application<GLFWApplicationPeer, GLFWApplicationListener>() {
    var fullscreen = false

    private var window: Long by Delegates.notNull()

    private val monitor: Long by lazy {
        glfwGetPrimaryMonitor()
    }
    private val videoMode: GLFWVidMode by lazy {
        glfwGetVideoMode(monitor)
    }

    private val errorCallback = GLFWErrorCallback.createPrint(System.err)

    private val keyCallback =
        GLFWKeyCallback.create { window: Long, key: Int, scanCode: Int, action: Int, mods: Int ->
            notifyListeners {
                it.onKeyType(window, key, scanCode, action, mods)
            }

        }

    private val resizeCallback = GLFWWindowSizeCallback.create { window, width, height ->
        onResize(width, height)
    }

    private val cursorPositionCallback = GLFWCursorPosCallback.create { window, x, y ->
        notifyListeners {
            it.onMouseMove(window, x, y)
        }
    }

    private val mouseClickCallback = GLFWMouseButtonCallback.create { window, button, action, mods ->
        notifyListeners {
            it.onMouseClick(window, button, action, mods)
        }
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
        glfwSetKeyCallback(window, keyCallback)
        glfwSetCursorPosCallback(window, cursorPositionCallback)
        glfwSetMouseButtonCallback(window, mouseClickCallback)

        glfwMakeContextCurrent(window)
        backend.setupContext()
    }

    override fun cleanup() {
        errorCallback.free()
        backend.cleanup()
        keyCallback.free()
        resizeCallback.free()
        cursorPositionCallback.free()
        mouseClickCallback.free()
    }

    override fun shouldTerminate() = glfwWindowShouldClose(window)

    override fun pollEvents() {
        glfwPollEvents()
    }

    override fun flush() {
        glfwSwapBuffers(window)
    }

    override fun time() = glfwGetTime()

    override fun wrapPeer(base: ApplicationPeer): GLFWApplicationPeer {
        return object: GLFWApplicationPeer(base) {
            override val window: Long
                get() {
                    return this@GLFWApplication.window
                }
        }
    }
}
