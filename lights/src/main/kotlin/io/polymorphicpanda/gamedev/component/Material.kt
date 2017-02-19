package io.polymorphicpanda.gamedev.component

import org.joml.Vector3f
import org.pandaframework.ecs.component.Component

/**
 * @author Ranie Jade Ramiso
 */
data class Material(val albedo: Vector3f = Vector3f(),
                    var metallic: Float = 0.0f,
                    var roughness: Float = 0.0f,
                    var ao: Float = 1.0f): Component
