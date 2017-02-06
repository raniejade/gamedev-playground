package org.pandaframework.lwjgl


inline val Byte.Companion.SIZE: Int
    get() = 8

inline val Float.Companion.BYTES: Int
    get() = 4

inline val Float.Companion.SIZE: Int
    get() = BYTES * Byte.SIZE
