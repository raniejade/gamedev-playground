package io.polymorphicpanda.gamedev.component

import org.joml.Quaternionf
import org.joml.Vector3f
import org.pandaframework.ecs.component.Component

/**
 * @author Ranie Jade Ramiso
 */
data class Transform(val position: Vector3f = Vector3f(0.0f),
                     val rotation: Quaternionf = Quaternionf()): Component
