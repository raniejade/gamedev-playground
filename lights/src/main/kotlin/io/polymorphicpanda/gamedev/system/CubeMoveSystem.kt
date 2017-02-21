package io.polymorphicpanda.gamedev.system

import io.polymorphicpanda.gamedev.GameState
import io.polymorphicpanda.gamedev.component.Cube
import io.polymorphicpanda.gamedev.component.Transform
import org.joml.Math
import org.pandaframework.ecs.aspect.AspectBuilder
import org.pandaframework.ecs.entity.Entity
import org.pandaframework.ecs.system.IteratingSystem
import org.pandaframework.ecs.system.System
import org.pandaframework.ecs.system.UpdateStrategies
import org.pandaframework.ecs.system.UpdateStrategy


private const val RADIUS = 1.0f
/**
 * @author Ranie Jade Ramiso
 */
class CubeMoveSystem: System<GameState>(), IteratingSystem {
    private val cubeMapper by mapper<Cube>()
    private val transformMapper by mapper<Transform>()

    override val supportedStates: Array<GameState>
        get() = arrayOf(GameState.Initial)

    override fun updateStrategy(): UpdateStrategy {
        return with(UpdateStrategies) {
            iterating(this@CubeMoveSystem)
        }
    }

    override fun AspectBuilder.aspect() {
        allOf(Cube::class)
    }

    override fun update(time: Double, entity: Entity) {
        val cube = cubeMapper.get(entity)
        cube.angle += time

        with(transformMapper.get(entity)) {
            position.x = (Math.sin(cube.angle) * RADIUS).toFloat()
            position.z = (Math.cos(cube.angle) * RADIUS).toFloat()
        }
    }
}
