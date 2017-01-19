package org.pandaframework.asset

/**
 * @author Ranie Jade Ramiso
 */
class ClasspathAssetDirectory(val root: String, val classLoader: ClassLoader): AssetDirectory {
    override fun asset(path: String): Asset {
        return object: Asset {
            override fun inputStream() = classLoader.getResourceAsStream("$root/${cleanPath(path)}")
        }
    }

    override fun directory(path: String): AssetDirectory {
        return ClasspathAssetDirectory("$root/${cleanPath(path)}", classLoader)
    }

    private fun cleanPath(path: String) = path.trim('/')
}
