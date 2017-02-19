package io.polymorphicpanda.gamedev

import org.pandaframework.ecs.state.State

/**
 * @author Ranie Jade Ramiso
 */
sealed class GameState: State {
    object Initial: GameState()
}
