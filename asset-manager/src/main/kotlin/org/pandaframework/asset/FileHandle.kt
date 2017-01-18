package org.pandaframework.asset

import java.io.InputStream

/**
 * @author Ranie Jade Ramiso
 */
interface FileHandle {
    fun inputStream(): InputStream
}
