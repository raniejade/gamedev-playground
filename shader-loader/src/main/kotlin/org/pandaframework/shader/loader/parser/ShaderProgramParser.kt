package org.pandaframework.shader.loader.parser

import java.io.InputStream

/**
 * @author Ranie Jade Ramiso
 */
interface ShaderProgramParser {
    fun parse(source: InputStream): ShaderProgramDescriptor
}
