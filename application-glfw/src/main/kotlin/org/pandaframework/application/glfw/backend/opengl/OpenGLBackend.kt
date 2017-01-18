package org.pandaframework.application.glfw.backend.opengl

import org.lwjgl.glfw.GLFW.*
import org.pandaframework.application.glfw.backend.Backend

/**
 * @author Ranie Jade Ramiso
 */
class OpenGLBackend private constructor(
    val version: Pair<Int, Int>?,
    val profile: Int,
    val vsync: Boolean
): Backend {
    override fun setupWindowHints() {

        if (version != null) {
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, version.first)
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, version.second)
        }

        glfwWindowHint(GLFW_OPENGL_PROFILE, profile)

    }

    override fun setupContext() {
        if (vsync) {
            glfwSwapInterval(1)
        }
    }

    override fun cleanup() {
    }

    class Builder internal constructor() {
        private var version: Pair<Int, Int>? = null
        private var profile: Int = GLFW_OPENGL_ANY_PROFILE
        private var vsync: Boolean = true

        fun version(major: Int, minor: Int): Builder {
            version = Pair(major, minor)
            return this
        }

        fun profile(profile: Int): Builder {
            this.profile = profile
            return this
        }

        fun vsync(enabled: Boolean): Builder {
            vsync = enabled
            return this
        }

        fun build() = OpenGLBackend(version, profile, vsync)
    }

    companion object {
        fun create(): Builder = Builder()
    }
}
