package org.pandaframework.shader.compiler

/**
 * @author Ranie Jade Ramiso
 */
sealed class CompileResult {
    class Success: CompileResult()
    class Error(val error: String): CompileResult()
}
