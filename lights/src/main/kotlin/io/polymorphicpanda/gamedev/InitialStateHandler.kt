package io.polymorphicpanda.gamedev

import io.polymorphicpanda.gamedev.component.Cube
import org.pandaframework.ecs.entity.Entity
import org.pandaframework.ecs.state.StateHandler

/**
 * @author Ranie Jade Ramiso
 */
class InitialStateHandler: StateHandler<GameState.Initial>() {
    private val cubeBluePrint by blueprint {
        withComponent<Cube>()
    }

    private val entities = mutableListOf<Entity>()

    override fun cleanup() {
        entities.forEach(entityManager::destroy)
    }

    override fun setup() {
        entities.add(cubeBluePrint.create())
    }
}
