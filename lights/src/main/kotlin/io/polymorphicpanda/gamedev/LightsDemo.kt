package io.polymorphicpanda.gamedev

import io.polymorphicpanda.gamedev.shader.UniformBufferManager
import io.polymorphicpanda.gamedev.system.CameraSystem
import io.polymorphicpanda.gamedev.system.CubeMoveSystem
import io.polymorphicpanda.gamedev.system.RenderSystem
import org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.pandaframework.application.glfw.GLFWApplication
import org.pandaframework.application.glfw.GLFWApplicationListener
import org.pandaframework.application.glfw.backend.Backend
import org.pandaframework.application.glfw.backend.opengl.OpenGLBackend
import org.pandaframework.ecs.createWorld


/**
 * @author Ranie Jade Ramiso
 */
class LightsDemo: GLFWApplicationListener() {
    private val uniformBufferManager = UniformBufferManager()
    private val cameraSystem = CameraSystem(uniformBufferManager)

    private val world by lazy {
        createWorld<GameState> {
            registerStateHandler(GameState.Initial, InitialStateHandler())

            registerSystem(cameraSystem)
            registerSystem(CubeMoveSystem())
            registerSystem(RenderSystem(uniformBufferManager))

            initialState(GameState.Initial)
        }
    }

    override fun setup() {
        GL.createCapabilities(false)

        world.setup()

        cameraSystem.adjustProjection(getWidth(), getHeight())
    }

    override fun cleanup() {
        world.cleanup()
    }

    override fun resize(width: Int, height: Int) {
        cameraSystem.adjustProjection(width, height)
    }

    override fun frameBufferResize(width: Int, height: Int) {
        GL11.glViewport(0, 0, width, height)
    }

    override fun update(time: Double) {
        world.update(time)
    }
}


fun main(vararg args: String) {
    val backend: Backend = OpenGLBackend.create()
        .version(3, 3)
        .profile(GLFW_OPENGL_CORE_PROFILE)
        .sampleSize(4)
        .forwardCompatible(true) /* true for OSX */
        .build()

    val application = GLFWApplication(backend).apply {
        title = "PBR Demo | OpenGL"
        addApplicationListener(LightsDemo())
    }

    application.start()
}
