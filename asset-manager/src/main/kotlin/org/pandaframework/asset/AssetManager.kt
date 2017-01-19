package org.pandaframework.asset

/**
 * @author Ranie Jade Ramiso
 */
class AssetManager(private val root: AssetDirectory) {
    fun asset(path: String) = root.asset(path)
    fun directory(path: String) = root.directory(path)
}
