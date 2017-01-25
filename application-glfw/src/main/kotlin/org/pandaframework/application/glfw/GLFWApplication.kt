package org.pandaframework.application.glfw

import org.lwjgl.glfw.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.system.MemoryUtil.NULL
import org.pandaframework.application.Application
import org.pandaframework.application.ApplicationPeer
import org.pandaframework.application.glfw.backend.Backend
import kotlin.properties.Delegates

/**
 * @author Ranie Jade Ramiso
 */
class GLFWApplication(val backend: Backend): Application<GLFWApplicationPeer, GLFWApplicationListener>() {
    var fullscreen = false

    private var _width: Int by Delegates.notNull()
    private var _height: Int by Delegates.notNull()

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
                it.onKeyType(key, scanCode, action, mods)
            }

        }

    private val resizeCallback = GLFWWindowSizeCallback.create { window, width, height ->
        _width = width
        _height = height

        onResize(width, height)
    }

    private val cursorPositionCallback = GLFWCursorPosCallback.create { window, x, y ->
        notifyListeners {
            it.onMouseMove(x, y)
        }
    }

    private val mouseClickCallback = GLFWMouseButtonCallback.create { window, button, action, mods ->
        notifyListeners {
            it.onMouseClick(button, action, mods)
        }
    }

    override fun setup() {
        if (!glfwInit()) {
            throw GLFWApplicationException("Failed to initialize GLFW.")
        }

        glfwSetErrorCallback(errorCallback)
        backend.setupWindowHints()

        val monitor = if (fullscreen) {
            this.monitor
        } else {
            NULL
        }

        _width = videoMode.width()
        _height = videoMode.height()


        window = glfwCreateWindow(_width, _height, title, monitor, NULL)

        if (window == NULL) {
            throw GLFWApplicationException("Failed to create a window.")
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
        glfwTerminate()
    }

    override fun getWidth() = _width

    override fun getHeight() = _height

    override fun shouldTerminate() = glfwWindowShouldClose(window)

    override fun pollEvents() {
        glfwPollEvents()
    }

    override fun flush() {
        glfwSwapBuffers(window)
    }

    override fun requestShutdown() {
        if (isInitialized()) {
            glfwSetWindowShouldClose(window, true)
        }
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
