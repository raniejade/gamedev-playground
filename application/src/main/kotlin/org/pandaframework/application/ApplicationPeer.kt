package org.pandaframework.application

/**
 * @author Ranie Jade Ramiso
 */
interface ApplicationPeer {
    fun getWidth(): Int
    fun getHeight(): Int
    fun getFps(): Double
    fun requestShutdown()
}
