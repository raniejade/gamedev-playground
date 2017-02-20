package io.polymorphicpanda.gamedev.shader

import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL31

const val N = 4
const val VECTOR3_SIZE = 4 * N
const val VECTOR4_SIZE = 4 * N
const val MAT4_SIZE = 4 * VECTOR4_SIZE
const val INT_SIZE = 4

const val LIGHT_SIZE = VECTOR3_SIZE * 2

const val MAX_LIGHTS = 255

/**
 * @author Ranie Jade Ramiso
 */
class UniformBufferManager {
    val constants by lazy {
        GL15.glGenBuffers().apply {
            GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, this)
            GL15.glBufferData(GL31.GL_UNIFORM_BUFFER, constantsBlockSize, GL15.GL_STATIC_DRAW)
            GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0)
        }
    }

    val lights by lazy {
        GL15.glGenBuffers().apply {
            GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, this)
            GL15.glBufferData(GL31.GL_UNIFORM_BUFFER, lightsBlockSize, GL15.GL_STATIC_DRAW)
            GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0)
        }
    }

    val constantsBlockSize: Long = (MAT4_SIZE * 2L) + VECTOR3_SIZE

    val lightsBlockSize: Long = INT_SIZE + 12L + (LIGHT_SIZE * MAX_LIGHTS)
}
