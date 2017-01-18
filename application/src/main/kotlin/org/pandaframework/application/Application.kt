package org.pandaframework.application

import org.pandaframework.application.util.FpsCounter
import java.util.LinkedList
import kotlin.properties.Delegates

/**
 * @author Ranie Jade Ramiso
 */
abstract class Application<T: ApplicationPeer, K: ApplicationListener<T>> {
    var title: String by Delegates.notNull()

    private var fpsCounter = FpsCounter()

    private val peer: T by lazy {
        wrapPeer(object: ApplicationPeer {
            override fun getFps() = fpsCounter.fps()
        })
    }

    protected val listeners = LinkedList<K>()

    fun addApplicationListener(listener: K) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    fun start() {
        setup()

        notifyListeners {
            it.peer = peer
        }

        notifyListeners(ApplicationListener<T>::setup)

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

        notifyListeners(ApplicationListener<T>::cleanup)

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

    protected abstract fun wrapPeer(base: ApplicationPeer): T

    protected inline fun notifyListeners(callback: (K) -> Unit) {
        listeners.forEach(callback)
    }
}
