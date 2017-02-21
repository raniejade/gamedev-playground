package io.polymorphicpanda.gamedev.system

import io.polymorphicpanda.gamedev.GameState
import io.polymorphicpanda.gamedev.Light
import io.polymorphicpanda.gamedev.component.Cube
import io.polymorphicpanda.gamedev.component.Material
import io.polymorphicpanda.gamedev.component.Plane
import io.polymorphicpanda.gamedev.component.Transform
import io.polymorphicpanda.gamedev.shader.PBRShader
import io.polymorphicpanda.gamedev.shader.UniformBufferManager
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL31
import org.lwjgl.system.MemoryUtil
import org.pandaframework.ecs.aspect.AspectBuilder
import org.pandaframework.ecs.entity.Entity
import org.pandaframework.ecs.entity.Mapper
import org.pandaframework.ecs.system.BasicSystem
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
class RenderSystem(private val uniformBufferManager: UniformBufferManager): System<GameState>(), BasicSystem {
    private val shader = PBRShader()

    private var cubeVao: Int by Delegates.notNull()
    private var planeVao: Int by Delegates.notNull()
    private val modelMatrix = Matrix4f()
    private val transformMapper: Mapper<Transform> by mapper()

    private val materialMapper: Mapper<Material> by mapper()
    private val cubeMapper: Mapper<Cube> by mapper()

    private val lights = mutableListOf<Light>()

    // we reuse this buffer every time we upload
    // matrix data to OpenGL
    private var matrixBuffer: FloatBuffer by Delegates.notNull()

    override val supportedStates: Array<GameState>
        get() = arrayOf(GameState.Initial)

    override fun updateStrategy(): UpdateStrategy {
        return with(UpdateStrategies) {
            basic(this@RenderSystem)
        }
    }

    override fun update(time: Double, entities: IntArray) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
        entities.forEach(this::render)
    }

    override fun AspectBuilder.aspect() {
        allOf(Transform::class, Material::class)
        anyOf(Cube::class, Plane::class)
    }

    fun render(entity: Entity) {
        bind(shader) {
            with(transformMapper.get(entity)) {
                modelMatrix.translation(position)
                    .get(matrixBuffer)

                GL20.glUniformMatrix4fv(shader.model, false, matrixBuffer)
            }

            with(materialMapper.get(entity)) {
                matrixBuffer.position(13)
                albedo.get(matrixBuffer)
                GL20.glUniform3fv(shader.albedo, matrixBuffer)
                matrixBuffer.rewind()

                GL20.glUniform1f(shader.metallic, metallic)
                GL20.glUniform1f(shader.roughness, roughness)
                GL20.glUniform1f(shader.ao, ao)
            }

            val (vao, count) = if (cubeMapper.contains(entity)) {
                cubeVao to 36
            } else {
                planeVao to 6
            }

            GL30.glBindVertexArray(vao)
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, count)
            GL30.glBindVertexArray(0)
        }
    }

    override fun setup() {
        cubeVao = setupCubeVao()
        planeVao = setupPlaneVao()
        matrixBuffer = MemoryUtil.memAllocFloat(16)

        with(lights) {
            add(Light(Vector3f(0.0f, 0.0f, 0.0f), Vector3f(1.0f, 0.0f, 1.0f)))
            add(Light(Vector3f(0.0f, 1.0f, -1.0f), Vector3f(1.0f)))
            add(Light(Vector3f(0.0f, 1.0f, 0.0f), Vector3f(1.0f)))
            add(Light(Vector3f(0.0f, 1.0f, 1.0f), Vector3f(1.0f)))
            add(Light(Vector3f(1.0f, 1.0f, 1.0f), Vector3f(1.0f)))
            add(Light(Vector3f(-1.0f, 1.0f, 1.0f), Vector3f(1.0f)))
            add(Light(Vector3f(1.0f, -1.0f, 1.0f), Vector3f(1.0f)))
        }

        stackPush {
            val countBuffer = mallocInt(1)
            countBuffer.put(lights.size)
            countBuffer.flip()

            // we only use 6 floats (3 for position, 3 for color)
            // but since vec3 is 4N aligned we pad each vec3 with N
            // making it act like a vec4
            val buffer = mallocFloat(lights.size * 8)
            lights.forEachIndexed { index, (position, color) ->
                val offset = index * 8
                position.get(offset, buffer)
                color.get(offset + 4, buffer)
            }

            with(uniformBufferManager) {
                GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, lights)
                GL15.glBufferSubData(GL31.GL_UNIFORM_BUFFER, 0, countBuffer)
                GL15.glBufferSubData(GL31.GL_UNIFORM_BUFFER, 16, buffer)
                GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0)

                GL30.glBindBufferRange(GL31.GL_UNIFORM_BUFFER, shader.constants, constants, 0, constantsBlockSize)
                GL30.glBindBufferRange(GL31.GL_UNIFORM_BUFFER, shader.lights, lights, 0, lightsBlockSize)
            }
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glEnable(GL13.GL_MULTISAMPLE)
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
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

        return setupVao(vertices)
    }

    private fun setupPlaneVao(): Int {
        val vertices = floatArrayOf(
            -5.0f, 0.0f, 5.0f, 0.0f, 1.0f, 0.0f,
            5.0f, 0.0f, 5.0f, 0.0f, 1.0f, 0.0f,
            -5.0f, 0.0f, -5.0f, 0.0f, 1.0f, 0.0f,

            -5.0f, 0.0f, -5.0f, 0.0f, 1.0f, 0.0f,
            5.0f, 0.0f, -5.0f, 0.0f, 1.0f, 0.0f,
            5.0f, 0.0f, 5.0f, 0.0f, 1.0f, 0.0f

        )

        return setupVao(vertices)
    }

    private fun setupVao(vertices: FloatArray): Int {
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
