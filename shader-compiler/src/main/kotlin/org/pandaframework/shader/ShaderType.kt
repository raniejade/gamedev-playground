package org.pandaframework.shader

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author Ranie Jade Ramiso
 */
enum class ShaderType {
    @JsonProperty("vertex")
    VERTEX,

    @JsonProperty("fragment")
    FRAGMENT
}
