package io.polymorphicpanda.gamedev

import io.polymorphicpanda.gamedev.component.Cube
import io.polymorphicpanda.gamedev.component.Material
import org.pandaframework.ecs.entity.Entity
import org.pandaframework.ecs.entity.Mapper
import org.pandaframework.ecs.state.StateHandler

/**
 * @author Ranie Jade Ramiso
 */
class InitialStateHandler: StateHandler<GameState.Initial>() {
    private val cubeBluePrint by blueprint {
        withComponent<Cube>()
        withComponent<Material>()
    }

    private val materialMapper: Mapper<Material> by mapper()

    private val entities = mutableListOf<Entity>()

    override fun cleanup() {
        entities.forEach(entityManager::destroy)
    }

    override fun setup() {
        cubeBluePrint.create().apply {
            with(materialMapper.get(this)) {
                albedo.set(0.026f, 0.246f, 0.026f)
                metallic = 0.1f
                roughness = 0.04f
            }

            entities.add(this)
        }
    }
}
