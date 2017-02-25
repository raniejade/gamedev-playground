package io.polymorphicpanda.gamedev.system

import io.polymorphicpanda.gamedev.GameState
import io.polymorphicpanda.gamedev.shader.UniformBufferManager
import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL31
import org.lwjgl.system.MemoryUtil
import org.pandaframework.ecs.aspect.AspectBuilder
import org.pandaframework.ecs.system.System
import org.pandaframework.ecs.system.UpdateStrategy
import java.nio.FloatBuffer
import kotlin.properties.Delegates

/**
 * @author Ranie Jade Ramiso
 */
class CameraSystem(private val uniformBufferManager: UniformBufferManager): System<GameState>(), UpdateStrategy {
    private val cameraPosition = Vector3f(0.0f, 5.0f, 5.0f)

    private val viewMatrix = Matrix4f()
    private val projectionMatrix = Matrix4f()

    // we reuse this buffer every time we upload
    // matrix data to OpenGL
    private var matrixBuffer: FloatBuffer by Delegates.notNull()

    override val supportedStates: Array<GameState>
        get() = arrayOf(GameState.Initial)

    override fun updateStrategy(): UpdateStrategy {
        return this
    }

    override fun AspectBuilder.aspect() { }

    override fun update(time: Double) {
        projectionMatrix.get(0, matrixBuffer)

        viewMatrix.identity()
            .lookAt(cameraPosition, Vector3f(0.0f, 0.0f, 0.0f), Vector3f(0.0f, 1.0f, 0.0f))
            .get(16, matrixBuffer)

        cameraPosition.get(32, matrixBuffer)

        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, uniformBufferManager.constants)
        GL15.glBufferSubData(GL31.GL_UNIFORM_BUFFER, 0, matrixBuffer)
        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0)
    }

    override fun setup() {
        matrixBuffer = MemoryUtil.memAllocFloat(16 * 2 + 3)
    }

    override fun cleanup() {
        MemoryUtil.memFree(matrixBuffer)
    }

    fun adjustProjection(width: Int, height: Int) {
        projectionMatrix
            .identity()
            .perspective(Math.toRadians(45.0).toFloat(), width.toFloat() / height, 0.1f, 1000.0f)
    }
}
