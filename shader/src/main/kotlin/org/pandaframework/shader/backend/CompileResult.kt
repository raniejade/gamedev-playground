package org.pandaframework.shader.backend

/**
 * @author Ranie Jade Ramiso
 */
sealed class CompileResult {
    class Success(val shader: Int): CompileResult()
    class Error(val error: String): CompileResult()
}
