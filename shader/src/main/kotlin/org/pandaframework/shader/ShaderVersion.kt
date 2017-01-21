package org.pandaframework.shader

/**
 * @author Ranie Jade Ramiso
 */
data class ShaderVersion(val version: String, val profile: Profile) {
    enum class Profile {
        Core, Compatibility;

        override fun toString(): String {
            return name.toLowerCase()
        }
    }
}
