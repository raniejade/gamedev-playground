package io.polymorphicpanda.gamedev.shader

import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL31
import org.pandaframework.lwjgl.BYTES

val VECTOR3_SIZE = Float.BYTES * 4
val MATRIX_SIZE = 16L * Float.BYTES


/**
 * @author Ranie Jade Ramiso
 */
class UniformBufferManager {
    val constants by lazy {
        val buffer = GL15.glGenBuffers()
        buffer.apply {
            GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, this)
            GL15.glBufferData(GL31.GL_UNIFORM_BUFFER, constantsBlockSize, GL15.GL_STATIC_DRAW)
            GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0)
        }
    }

    val constantsBlockSize = (MATRIX_SIZE * 2) + VECTOR3_SIZE
}
