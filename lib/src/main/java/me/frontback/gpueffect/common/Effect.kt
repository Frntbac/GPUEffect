/*
 * Copyright (C) 2017 Social Apps BVBA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.frontback.gpueffect.common

import android.graphics.Bitmap
import java.nio.FloatBuffer

interface Effect : Input {

    /**
     * Returns the output width
     *
     * @return Output width
     */
    val outputWidth: Int

    /**
     * Returns the output height
     *
     * @return Output height
     */
    val outputHeight: Int

    /**
     * Texture coordinates array used for [GLSLProgram.TEXTURE_COORDINATE]
     */
    var rotationArray: FloatArray

    /**
     * Returns whether or not this Effect has initialized
     *
     * @return Whether or not this Effect has initialized
     */
    val isInitialized: Boolean

    /**
     * Texture attached to be used as input
     */
    var input: Input?

    var renderFrameBuffer: FrameBuffer?

    /**
     * Returns the texture in which it will draw itself if any
     *
     * @return the texture in which it will draw itself if any
     */
    val outputTexture: Texture?

    /** @inheritdoc */
    override val texture: Texture?
        get() = outputTexture

    /**
     * Initialize all objects attached to the effect
     */
    fun init()

    /**
     * Called when the object has been initialized
     */
    fun onInit()

    /**
     * Sets the [FrameBuffer] in which to draw
     */
    infix fun drawsInto(frameBuffer: FrameBuffer?) = also {
        renderFrameBuffer = frameBuffer
    }

    /**
     * Draw the effect
     * @return the texture in which it drew if any
     */
    fun draw() : Texture?

    /**
     * Called before starting to draw
     */
    fun onPreDraw()

    /**
     * Draw the effect
     * @return the texture in which it drew if any
     */
    fun onDraw(cubeBuffer: FloatBuffer, textureBuffer: FloatBuffer) : Texture?

    /**
     * Destroy all objects attached to the effect
     */
    override fun destroy()

    /**
     * Called before destroying the attached objects
     */
    fun onPreDestroy()

    /**
     * Called when the attached objects have been destroyed
     */
    fun onDestroy()

    /**
     * @throws IllegalArgumentException
     */
    fun setRotation(@Rotation rotation: Int) = also {
        rotationArray = getRotation(rotation)
    }

    @Rotation
    fun getRotation() : Int

    /**
     * Generate a bitmap for this Effect
     *
     * @return Generated bitmap for this Effect
     */
    val bitmap: Bitmap
        get() {
            if (outputWidth < 1 || outputHeight < 1) {
                throw IllegalStateException("Output width and height must be >= 1")
            }
            val buffer = PixelBuffer(outputWidth, outputHeight)
            buffer.effect = this
            val bitmap = buffer.bitmap
            buffer.destroy()
            return bitmap
        }

    /**
     * Sets the desired output size
     *
     * @param width desired output width
     * @param height desired output height
     */
    fun setOutputSize(width: Int, height: Int): Effect

    /**
     * Sets the input of this effect
     * @param input used as input
     * @return this
     */
    infix fun receives(input: Input?) = also {
        this.input = input
    }

    /**
     * Sets the input of this effect
     *
     * @param bitmap from with to create the [BitmapTexture] as input
     * @return this
     */
    infix fun receives(bitmap: Bitmap) = also {
        setOutputSize(bitmap.width, bitmap.height)
        val input = BitmapTexture(bitmap)
        this.input = input
    }

    companion object {

        internal val CUBE = floatArrayOf(-1f, -1f, // bottom left
                +1f, -1f, // bottom right
                -1f, +1f, // top left
                +1f, +1f)// top right

        fun getRotation(@Rotation rotation: Int) = when (rotation) {
            Rotation.NONE -> Rotation.ARRAY_NONE
            Rotation._90 -> Rotation.ARRAY_90
            Rotation._180 -> Rotation.ARRAY_180
            Rotation._270 -> Rotation.ARRAY_270
            Rotation.UPSIDE_DOWN -> Rotation.ARRAY_UPSIDE_DOWN
            Rotation.UPSIDE_DOWN_90 -> Rotation.ARRAY_UPSIDE_DOWN_90
            Rotation.UPSIDE_DOWN_180 -> Rotation.ARRAY_UPSIDE_DOWN_180
            Rotation.UPSIDE_DOWN_270 -> Rotation.ARRAY_UPSIDE_DOWN_270
            else -> throw IllegalArgumentException("$rotation is not a valid rotation argument")
        }

        fun shouldInvertWidthAndHeight(rotation: Int) = rotation in
                intArrayOf(Rotation._90, Rotation._270, Rotation.UPSIDE_DOWN_90, Rotation.UPSIDE_DOWN_270)
    }
}