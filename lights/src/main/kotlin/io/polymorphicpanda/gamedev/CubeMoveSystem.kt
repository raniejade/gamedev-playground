package io.polymorphicpanda.gamedev

import io.polymorphicpanda.gamedev.component.Cube
import org.joml.Math
import org.pandaframework.ecs.aspect.AspectBuilder
import org.pandaframework.ecs.entity.Entity
import org.pandaframework.ecs.entity.Mapper
import org.pandaframework.ecs.system.IteratingSystem
import org.pandaframework.ecs.system.System
import org.pandaframework.ecs.system.UpdateStrategies
import org.pandaframework.ecs.system.UpdateStrategy


private const val RADIUS = 1.0f
/**
 * @author Ranie Jade Ramiso
 */
class CubeMoveSystem: System<GameState>(), IteratingSystem {
    private val cubeMapper: Mapper<Cube> by mapper()

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
        with(cubeMapper.get(entity)) {
            rotation.integrate(time.toFloat(), 0.0f, 1.0f, 0.0f)
            angle += time

            position.x = (Math.sin(angle) * RADIUS).toFloat()
            position.z = (Math.cos(angle) * RADIUS).toFloat()
        }
    }
}
