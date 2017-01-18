package org.pandaframework.application

import org.pandaframework.application.util.FpsCounter
import java.util.LinkedList
import kotlin.properties.Delegates

/**
 * @author Ranie Jade Ramiso
 */
abstract class Application {
    var title: String by Delegates.notNull()

    private var fpsCounter = FpsCounter()

    private var peer = object: ApplicationPeer {
        override fun getFps() = fpsCounter.fps()

    }

    private val listeners = LinkedList<ApplicationListener>()

    fun addApplicationListener(listener: ApplicationListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    fun start() {
        setup()

        notifyListeners {
            it.peer = peer
        }

        notifyListeners(ApplicationListener::setup)

        var timeSinceLastFrame = 0.0

        while (!shouldTerminate()) {
            val currentTime = time()
            val timeDelta = currentTime - timeSinceLastFrame

            fpsCounter.update(timeDelta)

            pollEvents()

            notifyListeners { it.update(timeDelta) }

            flush()

            timeSinceLastFrame = currentTime
        }

        notifyListeners(ApplicationListener::cleanup)

        cleanup()
    }

    protected fun onResize(width: Int, height: Int) {
        notifyListeners { it.resize(width, height) }
    }

    protected abstract fun setup()
    protected abstract fun cleanup()
    protected abstract fun shouldTerminate(): Boolean
    protected abstract fun pollEvents()
    protected abstract fun flush()
    protected abstract fun time(): Double

    private inline fun notifyListeners(callback: (ApplicationListener) -> Unit) {
        listeners.forEach(callback)
    }
}
