package org.pandaframework.application.glfw

import org.pandaframework.application.ApplicationPeer

/**
 * @author Ranie Jade Ramiso
 */
abstract class GLFWApplicationPeer(peer: ApplicationPeer): ApplicationPeer by peer {
    abstract val window: Long
}
