package org.pandaframework.application

import kotlin.properties.Delegates

/**
 * @author Ranie Jade Ramiso
 */
abstract class ApplicationListener<T: ApplicationPeer> {
    internal var peer: T by Delegates.notNull()

    open fun setup() { }
    open fun resize(width: Int, height: Int) { }
    open fun update(time: Double) {}
    open fun cleanup() { }

    open fun handleError(e: ApplicationException) {
        e.printStackTrace()
        requestShutdown()
    }

    fun getFps() = getPeer().getFps()

    fun requestShutdown() {
        peer.requestShutdown()
    }

    fun getWidth() = peer.getWidth()
    fun getHeight() = peer.getHeight()

    protected fun getPeer() = peer
}
