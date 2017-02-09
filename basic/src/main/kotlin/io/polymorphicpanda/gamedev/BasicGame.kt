package io.polymorphicpanda.gamedev

import io.polymorphicpanda.gamedev.shaders.BasicShader
import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.*
import org.lwjgl.system.MemoryUtil
import org.pandaframework.application.glfw.GLFWApplication
import org.pandaframework.application.glfw.GLFWApplicationListener
import org.pandaframework.application.glfw.backend.Backend
import org.pandaframework.application.glfw.backend.opengl.OpenGLBackend
import org.pandaframework.lwjgl.BYTES
import org.pandaframework.lwjgl.stackPush
import org.pandaframework.shader.using
import kotlin.properties.Delegates

class BasicGame: GLFWApplicationListener() {
    private var shader = BasicShader()
    private var vao: Int by Delegates.notNull()

    private val modelMatrix = Matrix4f()
    private val viewMatrix = Matrix4f()
    private val projectionMatrix = Matrix4f()
    private val cameraTarget = Vector3f(0.0f, 0.0f, 0.0f)
    private val cameraUp = Vector3f(0.0f, 1.0f, 0.0f)

    private var rotation = 0.0f

    private val matrixBuffer = MemoryUtil.memAllocFloat(16)

    override fun setup() {
        GL.createCapabilities(false)

        val vertices = floatArrayOf(
            // front
            0.5f,  0.5f, 0.0f,  // Top Right
            0.5f, -0.5f, 0.0f,  // Bottom Right
            -0.5f, -0.5f, 0.0f,  // Bottom Left
            -0.5f,  0.5f, 0.0f,   // Top Left

            // back
            0.5f,  0.5f, -1.0f,  // Top Right
            0.5f, -0.5f, -1.0f,  // Bottom Right
            -0.5f, -0.5f, -1.0f,  // Bottom Left
            -0.5f,  0.5f, -1.0f   // Top Left
        )
        val indices = intArrayOf(
            // front
            0, 1, 3,  // First Triangle
            1, 2, 3,   // Second Triangle

            // right
            0, 4, 5,
            0, 5, 1,

            // left
            3, 7, 6,
            3, 6, 2,

            // back
            4, 7, 6,
            6, 5, 4,

            // top
            0, 4, 7,
            3, 7, 0,

            // bottom
            1, 5, 6,
            2, 6, 1
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

            GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 3 * Float.BYTES, 0)
            GL20.glEnableVertexAttribArray(0)

            GL30.glBindVertexArray(0)
        }

        resize(getWidth(), getHeight())

        GL11.glEnable(GL11.GL_DEPTH_TEST)

        GL11.glClearColor(0.2f, 0.3f, 0.3f, 1.0f)
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE)
    }

    override fun update(time: Double) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)

        val time = glfwGetTime()
        rotation += Math.toRadians(Math.sin(time)).toFloat()

        using(shader) {
            val greenValue = Math.sin(time / 2) + 0.5f
            val redValue = Math.cos(time / 2) + 0.5f
            GL20.glUniform4f(shader.ourColor, redValue.toFloat(), greenValue.toFloat(), 0.0f, 1.0f)

            val radius = 10.0f
            val x = Math.sin(time) * radius
            val z = Math.cos(time) * radius

            modelMatrix.identity()
                .rotate(Math.toRadians(30.0).toFloat(), Vector3f(1.0f, 0.0f, 0.0f))

            viewMatrix.identity()
                .lookAt(Vector3f(x.toFloat(), 0f, z.toFloat()), cameraTarget, cameraUp)
//                .lookAt(Vector3f(0f, 0f, 3f), cameraTarget, cameraUp)

            modelMatrix.get(matrixBuffer)
            GL20.glUniformMatrix4fv(shader.modelMatrix, false, matrixBuffer)

            viewMatrix.get(matrixBuffer)
            GL20.glUniformMatrix4fv(shader.viewMatrix, false, matrixBuffer)

            projectionMatrix.get(matrixBuffer)
            GL20.glUniformMatrix4fv(shader.projectionMatrix, false, matrixBuffer)

            GL30.glBindVertexArray(vao)
            GL11.glDrawElements(GL11.GL_TRIANGLES, 36, GL11.GL_UNSIGNED_INT, 0)
            GL30.glBindVertexArray(0)
        }
    }

    override fun cleanup() {
        shader.delete()
        MemoryUtil.memFree(matrixBuffer)
    }

    override fun resize(width: Int, height: Int) {
        projectionMatrix.identity()
            .perspective(Math.toRadians(90.0).toFloat(), width.toFloat() / height, 0.1f, 1000f)
    }

    override fun frameBufferResize(width: Int, height: Int) {
        GL11.glViewport(0, 0, width, height)
    }

    override fun onKeyType(key: Int, scanCode: Int, action: Int, mods: Int) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
            glfwSetWindowShouldClose(window, true)
        } else if (key == GLFW_KEY_1 && action == GLFW_PRESS) {
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL)
        } else if (key == GLFW_KEY_2 && action == GLFW_PRESS) {
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN)
        } else if (key == GLFW_KEY_3 && action == GLFW_PRESS) {
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED)
        }
    }
}

fun main(vararg args: String) {
    val backend: Backend = OpenGLBackend.create()
        .version(3, 3)
        .profile(GLFW_OPENGL_CORE_PROFILE)
        .forwardCompatible(true) /* true for OSX */
        .build()

    val game = BasicGame()
    val application = GLFWApplication(backend).apply {
        title = "Basic OpenGL"
        addApplicationListener(game)
    }

    application.start()
}
