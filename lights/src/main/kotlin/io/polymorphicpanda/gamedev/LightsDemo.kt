package io.polymorphicpanda.gamedev

import io.polymorphicpanda.gamedev.shader.PhongShader
import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE
import org.lwjgl.glfw.GLFW.glfwGetTime
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

    private val cameraPosition = Vector3f()

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
        adjustProjection(width, height)
        uploadProjectionMatrix()
    }

    override fun frameBufferResize(width: Int, height: Int) {
        GL11.glViewport(0, 0, width, height)
    }

    override fun update(time: Double) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)

        updateAndUploadView(time)

        using(shader) {
            cubes.forEach { (position) ->
                val step = time * 0.2f
                val x = Math.sin(step + position.z) * RADIUS
                val y = Math.cos(step + position.z) * RADIUS
                val newPosition = Vector3f( x.toFloat(),  y.toFloat(), position.z)

                modelMatrix.translation(newPosition)
                    .get(matrixBuffer)

                GL20.glUniformMatrix4fv(shader.model, false, matrixBuffer)

                GL30.glBindVertexArray(vao)
                GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 36)
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

    private fun updateAndUploadView(time: Double) {
        val radius = 20.0f
        // fix me
        cameraPosition.x = Math.sin(time).toFloat() * radius
        cameraPosition.z = Math.cos(1000 / time).toFloat() * radius

        viewMatrix.identity()
            .lookAt(cameraPosition, Vector3f(0.0f, 0.0f, 0.0f), Vector3f(0.0f, 1.0f, 0.0f))
            .get(matrixBuffer)

        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, matricesUbo)
        GL15.glBufferSubData(GL31.GL_UNIFORM_BUFFER, MATRIX_SIZE, matrixBuffer)
        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0)
    }

    private fun setupCubeVao(): Int {
        val vertices = floatArrayOf(
            -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
            0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
            0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
            0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
            -0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
            -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,

            -0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,
            0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,
            0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,
            0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,
            -0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,
            -0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,

            -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,
            -0.5f,  0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
            -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
            -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
            -0.5f, -0.5f,  0.5f, -1.0f,  0.0f,  0.0f,
            -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,

            0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,
            0.5f,  0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
            0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
            0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
            0.5f, -0.5f,  0.5f,  1.0f,  0.0f,  0.0f,
            0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,

            -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,
            0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,
            0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
            0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
            -0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
            -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,

            -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,
            0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,
            0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
            0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
            -0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
            -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f
        )

        val vao = GL30.glGenVertexArrays()
        val vbo = GL15.glGenBuffers()


        stackPush {
            GL30.glBindVertexArray(vao)

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo)
            mallocFloat(vertices.size).let {
                it.put(vertices)
                it.flip()
                GL15.glBufferData(GL15.GL_ARRAY_BUFFER, it, GL15.GL_STATIC_DRAW)
            }

            GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 6 * Float.BYTES, 0L)
            GL20.glEnableVertexAttribArray(0)

            GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 6 * Float.BYTES, 3L * Float.BYTES)
            GL20.glEnableVertexAttribArray(1)

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
