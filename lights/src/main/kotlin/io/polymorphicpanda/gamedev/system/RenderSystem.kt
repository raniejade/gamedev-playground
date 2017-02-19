package io.polymorphicpanda.gamedev.system

import io.polymorphicpanda.gamedev.GameState
import io.polymorphicpanda.gamedev.component.Cube
import io.polymorphicpanda.gamedev.shader.PBRShader
import io.polymorphicpanda.gamedev.shader.UniformBufferManager
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL31
import org.lwjgl.system.MemoryUtil
import org.pandaframework.ecs.aspect.AspectBuilder
import org.pandaframework.ecs.entity.Entity
import org.pandaframework.ecs.entity.Mapper
import org.pandaframework.ecs.system.IteratingSystem
import org.pandaframework.ecs.system.System
import org.pandaframework.ecs.system.UpdateStrategies
import org.pandaframework.ecs.system.UpdateStrategy
import org.pandaframework.lwjgl.BYTES
import org.pandaframework.lwjgl.stackPush
import org.pandaframework.shader.bind
import java.nio.FloatBuffer
import kotlin.properties.Delegates

/**
 * @author Ranie Jade Ramiso
 */
class RenderSystem(private val uniformBufferManager: UniformBufferManager): System<GameState>(), IteratingSystem {
    private val shader = PBRShader()
    private var vao: Int by Delegates.notNull()
    private val modelMatrix = Matrix4f()

    private val cubeMapper: Mapper<Cube> by mapper()

    // we reuse this buffer every time we upload
    // matrix data to OpenGL
    private var matrixBuffer: FloatBuffer by Delegates.notNull()

    override val supportedStates: Array<GameState>
        get() = arrayOf(GameState.Initial)

    override fun updateStrategy(): UpdateStrategy {
        return with(UpdateStrategies) {
            iterating(this@RenderSystem)
        }
    }

    override fun AspectBuilder.aspect() {
        allOf(Cube::class)
    }

    override fun update(time: Double, entity: Entity) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)

        bind(shader) {
            with(cubeMapper.get(entity)) {
                modelMatrix.translation(position)
                    .get(matrixBuffer)

                GL20.glUniformMatrix4fv(shader.model, false, matrixBuffer)

                GL30.glBindVertexArray(vao)
                GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 36)
                GL30.glBindVertexArray(0)
            }
        }
    }

    override fun setup() {
        vao = setupCubeVao()
        matrixBuffer = MemoryUtil.memAllocFloat(16)

        with(uniformBufferManager) {
            GL30.glBindBufferRange(GL31.GL_UNIFORM_BUFFER, shader.constants, constants, 0, constantsBlockSize)
        }
    }

    override fun cleanup() {
        MemoryUtil.memFree(matrixBuffer)
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
