package org.pandaframework.asset.classpath

import org.pandaframework.asset.FileHandle
import org.pandaframework.asset.FileHandleResolver

/**
 * @author Ranie Jade Ramiso
 */
class ClasspathFileHandleResolver(private val classLoader: ClassLoader): FileHandleResolver {
    override fun resolve(path: String): FileHandle {
        return object: FileHandle {
            override fun inputStream() = classLoader.getResourceAsStream(path)
        }
    }
}
