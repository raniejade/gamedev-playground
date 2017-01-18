package org.pandaframework.asset

/**
 * @author Ranie Jade Ramiso
 */
interface FileHandleResolver {
    fun resolve(path: String): FileHandle
}
