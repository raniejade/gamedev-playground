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

    fun getFps() = peer.getFps()
}
