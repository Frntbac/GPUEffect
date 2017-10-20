package me.frontback.gpueffect.common

interface Input {
    /**
     * Texture that can be used as an input
     */
    val texture: Texture?

    /**
     * Destroy the input
     */
    fun destroy()
}