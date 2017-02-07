package io.polymorphicpanda.gamedev

import io.polymorphicpanda.gamedev.shader.PhongShader
import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL31
import org.lwjgl.system.MemoryUtil
import org.pandaframework.application.glfw.GLFWApplication
import org.pandaframework.application.glfw.GLFWApplicationListener
import org.pandaframework.application.glfw.backend.Backend
import org.pandaframework.application.glfw.backend.opengl.OpenGLBackend
import org.pandaframework.lwjgl.BYTES
import org.pandaframework.lwjgl.stackPush
import org.pandaframework.shader.using
import kotlin.properties.Delegates

val MATRIX_SIZE = 16L * Float.BYTES

val RADIUS = 10.0f

data class Cube(val position: Vector3f)

/**
 * @author Ranie Jade Ramiso
 */
class LightsDemo: GLFWApplicationListener() {
    private val shader = PhongShader()
    private var vao: Int by Delegates.notNull()

//    private val cubes = listOf(
//        Cube(Vector3f(0.0f, 0.0f, 0.0f)),
//        Cube(Vector3f(0.0f, 2.0f, 0.0f)),
//        Cube(Vector3f(0.0f, -2.0f, 0.0f))
//    )

    private val cubes = (0..50).map {
        val x = Math.sin(it.toDouble()) * RADIUS
        val y = Math.cos(it.toDouble()) * RADIUS

        Cube(Vector3f(x.toFloat(), y.toFloat(), -it.toFloat()))
    }

    private val matricesUbo by lazy {
        GL15.glGenBuffers()
    }

    private val viewMatrix = Matrix4f()
    private val projectionMatrix = Matrix4f()

    // reuse this for every cube
    private val modelMatrix = Matrix4f()

    // we reuse this buffer every time we upload
    // matrix data to OpenGL
    private val matrixBuffer = MemoryUtil.memAllocFloat(16)

    override fun setup() {
        GL.createCapabilities(false)

        vao = setupCubeVao()

        setupMatricesUniformBlock()
        adjustProjection(getWidth(), getHeight())
        uploadProjectionMatrix()

        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glClearColor(0.2f, 0.3f, 0.3f, 1.0f)
    }

    override fun resize(width: Int, height: Int) {
        GL11.glViewport(0, 0, width, height)
        adjustProjection(width, height)
        uploadProjectionMatrix()
    }

    override fun update(time: Double) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)

        uploadViewMatrix()

        using(shader) {
            cubes.forEach { (position) ->
                val x = Math.sin(glfwGetTime() + position.z) * RADIUS
                val y = Math.cos(glfwGetTime() + position.z) * RADIUS
                val newPosition = Vector3f( x.toFloat(),  y.toFloat(), position.z)

                modelMatrix.translation(newPosition)
                    .get(matrixBuffer)

                GL20.glUniformMatrix4fv(shader.model, false, matrixBuffer)

                GL30.glBindVertexArray(vao)
                GL11.glDrawElements(GL11.GL_TRIANGLES, 36, GL11.GL_UNSIGNED_INT, 0)
                GL30.glBindVertexArray(0)
            }

        }
    }

    override fun cleanup() {
        MemoryUtil.memFree(matrixBuffer)
    }

    private fun setupMatricesUniformBlock() {
        val blockSize = MATRIX_SIZE * 2
        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, matricesUbo)
        GL15.glBufferData(GL31.GL_UNIFORM_BUFFER, blockSize, GL15.GL_STATIC_DRAW)
        GL30.glBindBufferRange(GL31.GL_UNIFORM_BUFFER, shader.matrices, matricesUbo, 0, blockSize)
        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0)
    }

    private fun adjustProjection(width: Int, height: Int) {
        projectionMatrix
            .identity()
            .perspective(Math.toRadians(45.0).toFloat(), width.toFloat() / height, 0.1f, 1000.0f)
    }

    private fun uploadProjectionMatrix() {
        projectionMatrix.get(matrixBuffer)
        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, matricesUbo)
        GL15.glBufferSubData(GL31.GL_UNIFORM_BUFFER, 0, matrixBuffer)
        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0)
    }

    private fun uploadViewMatrix() {
        val x = Math.sin(glfwGetTime()) * 100
        val z = Math.cos(glfwGetTime()) * 100

        viewMatrix.identity()
            .lookAt(Vector3f(x.toFloat(), 0.0f, z.toFloat()), Vector3f(0.0f, 0.0f, 0.0f), Vector3f(0.0f, 1.0f, 0.0f))
            .get(matrixBuffer)

        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, matricesUbo)
        GL15.glBufferSubData(GL31.GL_UNIFORM_BUFFER, MATRIX_SIZE, matrixBuffer)
        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0)
    }

    private fun setupCubeVao(): Int {
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

        val vao = GL30.glGenVertexArrays()
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

        return vao
    }
}


fun main(vararg args: String) {
    val backend: Backend = OpenGLBackend.create()
        .version(3, 3)
        .profile(GLFW_OPENGL_CORE_PROFILE)
        .forwardCompatible(true) /* true for OSX */
        .build()

    val application = GLFWApplication(backend).apply {
        title = "Lights Demo | OpenGL"
        addApplicationListener(LightsDemo())
    }

    application.start()
}
