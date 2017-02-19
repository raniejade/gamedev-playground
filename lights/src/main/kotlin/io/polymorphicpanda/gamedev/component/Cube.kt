package io.polymorphicpanda.gamedev.component

import org.joml.Quaternionf
import org.joml.Vector3f
import org.pandaframework.ecs.component.Component

data class Cube(val position: Vector3f = Vector3f(0.0f),
                val rotation: Quaternionf = Quaternionf(),
                var angle: Double = 0.0): Component
