package org.pandaframework.shader.compiler

/**
 * @author Ranie Jade Ramiso
 */
sealed class LinkResult {
    class Success: LinkResult()
    class Error(val error: String): LinkResult()
}
