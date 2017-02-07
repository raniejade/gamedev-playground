package org.pandaframework.shader

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * @author Ranie Jade Ramiso
 */
abstract class ShaderProperty<out T>(val factory: (String) -> T): ReadOnlyProperty<Any, T> {
    private var value: T? = null

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        if (value == null) {
            value = factory(property.name)
        }
        return value!!
    }

}
