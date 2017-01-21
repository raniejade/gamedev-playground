package org.pandaframework.shader.backend

/**
 * @author Ranie Jade Ramiso
 */
sealed class LinkResult {
    class Success(val program: Int): LinkResult()
    class Error(val error: String): LinkResult()
}
