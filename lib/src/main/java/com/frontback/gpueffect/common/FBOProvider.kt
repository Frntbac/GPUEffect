package com.frontback.gpueffect.common

import java.util.*

/**
 * Class used to share FBOs between multiple GPUMultiEffect
 */
class FBOProvider {

    private val fbos = mutableMapOf<String, LinkedList<FrameBuffer>>()

    /**
     * Returns a FrameBuffer for the effect's output size
     * @param effect to take width and height from
     * @return Either a new FrameBuffer or an a released FrameBuffer
     */
    fun acquireFor(effect: Effect) = acquire(effect.outputWidth, effect.outputHeight)

    /**
     * Returns a FrameBuffer matching the width and height
     * @param width of the FrameBuffer's texture
     * @param height of the FrameBuffer's texture
     * @return Either a new FrameBuffer or an a released FBO
     */
    fun acquire(width: Int, height: Int): FrameBuffer {
        synchronized(fbos) {
            val list = listFor(width, height)
            return if (list.isNotEmpty()) {
                list.removeAt(0)
            } else {
                FBOTexture()
            }.also {
                if (!it.isInitialized) {
                    it.init(width, height)
                }
            }
        }
    }

    /**
     * Release the effect's FrameBuffer so that if can be given to other later
     * Also set the [Effect.renderFrameBuffer] to null
     * @param effect from which to release the FrameBuffer
     */
    fun releaseFor(effect: Effect) = effect.renderFrameBuffer?.let {
        release(it)
        effect.drawsInto(null)
    }

    /**
     * Release the FrameBuffer so that if can be given to others later
     * @param buffer to release
     */
    fun release(buffer: FrameBuffer?) {
        if (buffer != null) {
            synchronized(fbos) {
                val list = listFor(buffer.width, buffer.height)
                if (!list.contains(buffer)){
                    list.add(buffer)
                }
            }
        }
    }

    /**
     * Destroy all [FrameBuffer] hold and clears them
     */
    fun destroy() {
        for ((_, list) in fbos) {
            list.forEach {
                it.destroy()
            }
        }
        fbos.clear()
    }

    private fun listFor(width: Int, height: Int): LinkedList<FrameBuffer> {
        synchronized(fbos) {
            var list = fbos["$width*$height"]
            if (list == null) {
                list = LinkedList()
                fbos["$width*$height"] = list
            }
            return list
        }
    }
}