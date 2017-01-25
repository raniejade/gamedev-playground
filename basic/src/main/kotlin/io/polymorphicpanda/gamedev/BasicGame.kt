package io.polymorphicpanda.gamedev

import io.polymorphicpanda.gamedev.shaders.BasicShader
import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.system.MemoryUtil
import org.pandaframework.application.glfw.GLFWApplication
import org.pandaframework.application.glfw.GLFWApplicationListener
import org.pandaframework.application.glfw.backend.Backend
import org.pandaframework.application.glfw.backend.opengl.OpenGLBackend
import org.pandaframework.lwjgl.stackPush
import org.pandaframework.shader.using
import kotlin.properties.Delegates

class BasicGame: GLFWApplicationListener() {
    private var shader = BasicShader()
    private var vao: Int by Delegates.notNull()

    private val modelMatrix = Matrix4f()
    private val viewMatrix = Matrix4f()
    private val projectionMatrix = Matrix4f()

    private val matrixBuffer = MemoryUtil.memAllocFloat(16)

    override fun setup() {
        GL.createCapabilities(false)

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

        viewMatrix.lookAt(Vector3f(0f, 0f, 2f), Vector3f(0f, 0f, 0f), Vector3f(0f, 1f, 0f))
        projectionMatrix.perspective(Math.toRadians(45.0).toFloat(), getWidth().toFloat() / getHeight(), 0.1f, 100f)


        GL11.glClearColor(0.2f, 0.3f, 0.3f, 1.0f)
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE)
    }

    override fun update(time: Double) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)

        using(shader) {
            val greenValue = Math.sin(glfwGetTime() / 2) + 0.5
            GL20.glUniform4f(it.ourColor, 0.0f, greenValue.toFloat(), 0.0f, 1.0f)

            modelMatrix
                .rotate(Math.toRadians(Math.sin(glfwGetTime())).toFloat(), Vector3f(0f, 1f, 1f))


            modelMatrix.get(matrixBuffer)
            GL20.glUniformMatrix4fv(it.modelMatrix, false, matrixBuffer)

            viewMatrix.get(matrixBuffer)
            GL20.glUniformMatrix4fv(it.viewMatrix, false, matrixBuffer)

            projectionMatrix.get(matrixBuffer)
            GL20.glUniformMatrix4fv(it.projectionMatrix, false, matrixBuffer)

            GL30.glBindVertexArray(vao)
            GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0)
            GL30.glBindVertexArray(0)
        }
    }

    override fun cleanup() {
        shader.delete()
        MemoryUtil.memFree(matrixBuffer)
    }

    override fun resize(width: Int, height: Int) {
        projectionMatrix.identity()
            .perspective(Math.toRadians(90.0).toFloat(), width.toFloat() / height, 0.1f, 100f)

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
