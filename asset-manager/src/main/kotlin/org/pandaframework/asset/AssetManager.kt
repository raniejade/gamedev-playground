package org.pandaframework.asset

/**
 * @author Ranie Jade Ramiso
 */
class AssetManager(private val resolver: FileHandleResolver) {
    fun load(path: String): FileHandle = resolver.resolve(path)
}
