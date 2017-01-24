package org.pandaframework.shader

import org.pandaframework.shader.backend.ShaderBackend
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun Shader.uniform() = Uniform(this.backend)

/**
 * @author Ranie Jade Ramiso
 */
class Uniform(val backend: ShaderBackend): ReadOnlyProperty<Shader, Int> {
    private var location: Int? = null

    override fun getValue(thisRef: Shader, property: KProperty<*>): Int {
        if (location == null) {
            location = backend.getUniformLocation(thisRef.program, property.name)
        }

        return location!!
    }
}
