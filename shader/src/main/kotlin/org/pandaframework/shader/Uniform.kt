package org.pandaframework.shader

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun Shader.uniform() = Uniform(this)

/**
 * @author Ranie Jade Ramiso
 */
class Uniform(val shader: Shader): ReadOnlyProperty<Any, Int> {
    private var location: Int? = null

    override fun getValue(thisRef: Any, property: KProperty<*>): Int {
        if (location == null) {
            location = shader.backend.getUniformLocation(shader.program, property.name)
        }

        return location!!
    }
}
