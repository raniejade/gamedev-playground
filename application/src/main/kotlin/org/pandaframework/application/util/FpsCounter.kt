package org.pandaframework.application.util

/**
 * @author Ranie Jade Ramiso
 */
class FpsCounter {
    private var frames = 0
    private var totalTime = 0.0
    private var fps = 0.0


    fun update(elapsedTime: Double) {
        frames++
        totalTime += elapsedTime

        if (totalTime > 1.0) {
            fps = frames / totalTime
            frames = 0
            totalTime = 0.0

        }
    }

    fun fps() = fps
}
