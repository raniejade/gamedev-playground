package io.polymorphicpanda.gamedev

import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.pandaframework.application.Application
import org.pandaframework.application.ApplicationListener
import org.pandaframework.application.glfw.GLFWApplication
import org.pandaframework.application.glfw.GLFWKeyListener
import org.pandaframework.application.glfw.backend.Backend
import org.pandaframework.application.glfw.backend.opengl.OpenGLBackend
import org.pandaframework.lwjgl.stackPush
import java.io.InputStreamReader
import kotlin.properties.Delegates

object MyFileReader {
    fun read(path: String): String {
        return InputStreamReader(MyFileReader::class.java.classLoader.getResourceAsStream(path))
            .readText()
    }
}


class BasicApplication: ApplicationListener(), GLFWKeyListener {
    override fun handleKeyEvent(window: Long, key: Int, scanCode: Int, action: Int, mods: Int) {
        if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS) {
            GLFW.glfwSetWindowShouldClose(window, true)
        }
    }

    private var shaderProgram: Int by Delegates.notNull()
    private var vao: Int by Delegates.notNull()

    override fun setup() {
        GL.createCapabilities()

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

        shaderProgram = GL20.glCreateProgram()
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

        vao = GL30.glGenVertexArrays()
        val vbo = GL15.glGenBuffers()
        val ebo = GL15.glGenBuffers()


        stackPush {
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
//        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE)
    }

    override fun update(time: Double) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)

        GL20.glUseProgram(shaderProgram)
        GL30.glBindVertexArray(vao)
        GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0)
        GL30.glBindVertexArray(0)
    }

    override fun resize(width: Int, height: Int) {
        GL11.glViewport(0, 0, width, height)
    }
}

fun main(vararg args: String) {
    val backend: Backend = OpenGLBackend.create()
        .version(3, 3)
        .profile(GLFW.GLFW_OPENGL_CORE_PROFILE)
        .build()

    val engine = BasicApplication()
    val application: Application = GLFWApplication(backend).apply {
        title = "Basic OpenGL"
        keyListener = engine
        addApplicationListener(engine)
    }

    application.start()
}
