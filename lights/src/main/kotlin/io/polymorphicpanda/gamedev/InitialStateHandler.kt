package io.polymorphicpanda.gamedev

import io.polymorphicpanda.gamedev.component.Cube
import io.polymorphicpanda.gamedev.component.Material
import io.polymorphicpanda.gamedev.component.Plane
import io.polymorphicpanda.gamedev.component.Transform
import org.pandaframework.ecs.entity.Entity
import org.pandaframework.ecs.entity.Mapper
import org.pandaframework.ecs.state.StateHandler

/**
 * @author Ranie Jade Ramiso
 */
class InitialStateHandler: StateHandler<GameState.Initial>() {
    private val cubeBluePrint by blueprint {
        withComponent<Cube>()
        withComponent<Transform>()
        withComponent<Material>()
    }

    private val planeBluePrint by blueprint {
        withComponent<Plane>()
        withComponent<Transform>()
        withComponent<Material>()
    }

    private val materialMapper: Mapper<Material> by mapper()
    private val transformMapper: Mapper<Transform> by mapper()

    private val entities = mutableListOf<Entity>()

    override fun cleanup() {
        entities.forEach(entityManager::destroy)
    }

    override fun setup() {
        cubeBluePrint.create().apply {
            with(materialMapper.get(this)) {
                albedo.set(0.026f, 0.246f, 0.026f)
                metallic = 0.0f
                roughness = 0.025f
            }

            entities.add(this)
        }

        planeBluePrint.create().apply {
            with(materialMapper.get(this)) {
                albedo.set(0.78f, 0.54f, 0.38f)
                metallic = 0.0f
                roughness = 0.025f
            }

            with(transformMapper.get(this)) {
                position.y = -1.0f
            }

            entities.add(this)
        }
    }
}
