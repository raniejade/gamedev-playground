package io.polymorphicpanda.gamedev

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.glfw.GLFWWindowSizeCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil.NULL
import java.io.InputStreamReader

fun stackPush(block: MemoryStack.() -> Unit) {
    val stack = MemoryStack.stackPush()
    try {
        block(stack)
    } finally {
        stack.pop()
    }
}

object MyFileReader {
    fun read(path: String): String {
        return InputStreamReader(MyFileReader::class.java.classLoader.getResourceAsStream(path))
            .readText()

    }
}

const val WINDOW_WIDTH = 640
const val WINDOW_HEIGHT = 480

fun main(vararg args: String) {
    val errorCallback = GLFWErrorCallback.createPrint(System.err)
    glfwSetErrorCallback(errorCallback)

    if (!glfwInit()) {
        throw IllegalStateException("Unable to initialize GLFW")
    }

    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)

    val vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor())

    val window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "Simple Window", NULL, NULL)
    // fullscreen
    // val window = glfwCreateWindow(vidMode.width(), vidMode.height(), "Simple Window", glfwGetPrimaryMonitor(), NULL)

    if (window == NULL) {
        glfwTerminate()
        throw RuntimeException("Failed to create the GLFW window")
    }

    glfwSetWindowPos(window,
        (vidMode.width() - WINDOW_WIDTH) / 2,
        (vidMode.height() - WINDOW_HEIGHT) / 2
    )

    val resizeCallback = GLFWWindowSizeCallback.create { window, width, height ->
        GL11.glViewport(0, 0, width, height)
        glfwSetWindowAspectRatio(window, 16, 9)
    }

    glfwSetWindowSizeCallback(window, resizeCallback)

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

    // shader
    val vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER)
    GL20.glShaderSource(vertexShader, MyFileReader.read("vertex.glsl"))
    GL20.glCompileShader(vertexShader)

    if (GL20.glGetShaderi(vertexShader, GL20.GL_COMPILE_STATUS) != GL11.GL_TRUE) {
        throw RuntimeException(GL20.glGetShaderInfoLog(vertexShader))
    }

    val fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER)
    GL20.glShaderSource(fragmentShader, MyFileReader.read("fragment.glsl"))
    GL20.glCompileShader(fragmentShader)
    if (GL20.glGetShaderi(vertexShader, GL20.GL_COMPILE_STATUS) != GL11.GL_TRUE) {
        throw RuntimeException(GL20.glGetShaderInfoLog(fragmentShader))
    }

    val shaderProgram = GL20.glCreateProgram()
    GL20.glAttachShader(shaderProgram, vertexShader)
    GL20.glAttachShader(shaderProgram, fragmentShader)
    GL20.glLinkProgram(shaderProgram)

    if (GL20.glGetProgrami(shaderProgram, GL20.GL_LINK_STATUS) != GL11.GL_TRUE) {
        throw RuntimeException()
    }
    GL20.glDeleteShader(vertexShader)
    GL20.glDeleteShader(fragmentShader)

    val vertices = floatArrayOf(
        0.5f,  0.5f, 0.0f,  // Top Right
        0.5f, -0.5f, 0.0f,  // Bottom Right
        -0.5f, -0.5f, 0.0f,  // Bottom Left
        -0.5f,  0.5f, 0.0f   // Top Left
    )
    val indices = intArrayOf(
        0, 1, 3,  // First Triangle
        1, 2, 3   // Second Triangle
    )

    val vao = GL30.glGenVertexArrays()
    val vbo = GL15.glGenBuffers()
    val ebo = GL15.glGenBuffers()


    stackPush {
        val width = mallocInt(1)
        val height = mallocInt(1)

        glfwGetFramebufferSize(window, width, height)
        GL11.glViewport(0, 0, width.get(), height.get())

        GL30.glBindVertexArray(vao)

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo)
        mallocFloat(vertices.size).let {
            it.put(vertices)
            it.flip()
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, it, GL15.GL_STATIC_DRAW)
        }

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo)
        mallocInt(indices.size).let {
            it.put(indices)
            it.flip()
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, it, GL15.GL_STATIC_DRAW)
        }

        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 3 * 4, 0)
        GL20.glEnableVertexAttribArray(0)

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)

        GL30.glBindVertexArray(0)
    }



    GL11.glClearColor(0.2f, 0.3f, 0.3f, 1.0f)
//    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE)

    while (!glfwWindowShouldClose(window)) {
        glfwPollEvents()

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)

        GL20.glUseProgram(shaderProgram)
        GL30.glBindVertexArray(vao)
        GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0)
        GL30.glBindVertexArray(0)

        /* Swap buffers and poll Events */
        glfwSwapBuffers(window)

    }

    GL30.glDeleteVertexArrays(vao)
    GL15.glDeleteBuffers(vbo)
    GL15.glDeleteBuffers(ebo)

    /* Release window and its callbacks */
    glfwDestroyWindow(window)
    keyCallback.free()

    /* Terminate GLFW and release the error callback */
    glfwTerminate()
    errorCallback.free()
}
