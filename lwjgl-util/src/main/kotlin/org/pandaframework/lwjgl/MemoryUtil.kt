package org.pandaframework.lwjgl

import org.lwjgl.system.MemoryStack

fun stackPush(block: MemoryStack.() -> Unit) {
    val stack = MemoryStack.stackPush()
    try {
        block(stack)
    } finally {
        stack.pop()
    }
}
