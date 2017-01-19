package org.pandaframework.asset

/**
 * @author Ranie Jade Ramiso
 */
interface AssetDirectory {
    fun asset(path: String): Asset
    fun directory(path: String): AssetDirectory
}
