package io.polymorphicpanda.gamedev

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.system.MemoryUtil
import org.lwjgl.system.MemoryUtil.NULL


fun main(vararg args: String) {
    val errorCallback = GLFWErrorCallback.createPrint(System.err)
    glfwSetErrorCallback(errorCallback)

    if (!glfwInit()) {
        throw IllegalStateException("Unable to initialize GLFW")
    }

    val window = glfwCreateWindow(640, 480, "Simple Window", NULL, NULL)

    if (window == NULL) {
        glfwTerminate()
        throw RuntimeException("Failed to create the GLFW window")
    }

    val vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor())
    glfwSetWindowPos(window,
        (vidMode.width() - 640) / 2,
        (vidMode.height() - 480) / 2
    )

    /* Create OpenGL context */
    glfwMakeContextCurrent(window)
    GL.createCapabilities(true)


    /* Enable vertical synchronization */
    glfwSwapInterval(1)

    val keyCallback: GLFWKeyCallback = object: GLFWKeyCallback() {
        override fun invoke(window: Long, key: Int, scanCode: Int, action: Int, mods: Int) {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                glfwSetWindowShouldClose(window, true)
            }
        }

    }

    glfwSetKeyCallback(window, keyCallback)

    val width = MemoryUtil.memAllocInt(1)
    val height = MemoryUtil.memAllocInt(1)

    glfwGetFramebufferSize(window, width, height)
    GL11.glViewport(0, 0, width.get(), height.get())

    while (!glfwWindowShouldClose(window)) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)

        /* Swap buffers and poll Events */
        glfwSwapBuffers(window)
        glfwPollEvents()
    }

    /* Release window and its callbacks */
    glfwDestroyWindow(window)
    keyCallback.free()

    /* Terminate GLFW and release the error callback */
    glfwTerminate()
    errorCallback.free()
}
