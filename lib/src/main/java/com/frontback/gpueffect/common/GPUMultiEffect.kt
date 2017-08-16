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

package com.frontback.gpueffect.common

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

open class GPUMultiEffect(vararg _effects: Effect) : Effect {


    override final var outputWidth: Int = 0
        /**
         * {@inheritDoc}
         */
        get
        private set

    override final var outputHeight: Int = 0
        /**
         * {@inheritDoc}
         */
        get
        private set

    override var isInitialized: Boolean = false
        /**
         * {@inheritDoc}
         */
        get
        protected set

    /**
     * {@inheritDoc}
     */
    override var input: Texture? = null

    var firstFBO: FrameBuffer? = null
        private set
    var secondFBO: FrameBuffer? = null
        private set

    @Rotation
    private var rotation = Rotation.NONE

    val cubeBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(32)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer().apply { put(Effect.CUBE).flip() }
    }
    val textureBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(32)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer().apply { put(Effect.getRotation(rotation)).flip() }
    }

    val effects = mutableListOf<Effect>()

    /**
     * {@inheritDoc}
     */
    override var renderFrameBuffer: FrameBuffer? = null

    override val outputTexture: Texture?
        get() = renderFrameBuffer?.texture

    init {
        _effects.forEach {
            add(it)
        }
    }

    fun setExtraBuffers(first: FrameBuffer?, second: FrameBuffer?) {
        firstFBO = first
        secondFBO = second
    }

    override fun init() {
        renderFrameBuffer?.run {
            if (!isInitialized) {
                init(outputWidth, outputHeight)
            }
        }
        effects.forEach { it.init() }
        if (firstFBO == null) {
            firstFBO = FBOTexture()
        }
        if (!firstFBO!!.isInitialized) {
            firstFBO!!.init(outputWidth, outputHeight)
        }
        if (secondFBO == null) {
            secondFBO = FBOTexture()
        }
        if (!secondFBO!!.isInitialized) {
            secondFBO!!.init(outputWidth, outputHeight)
        }

        input?.run {
            if (!isInitialized) {
                init(outputWidth, outputHeight)
            }
        }
        onInit()
    }

    override fun setRotation(@Rotation rotation: Int) = also {
        super.setRotation(rotation)
        this.rotation = rotation
    }

    protected fun getRotation() = rotation

    override fun setRotationArray(array: FloatArray) {
        textureBuffer.clear()
        textureBuffer.put(array)
        textureBuffer.flip()
    }

    /**
     * {@inheritDoc}
     */
    override fun onInit() {

    }

    /**
     * {@inheritDoc}
     */
    override final fun draw() = onDraw(cubeBuffer, textureBuffer)

    /**
     * {@inheritDoc}
     */
    override fun onPreDraw() {

    }

    /**
     * {@inheritDoc}
     */
    override fun onDraw(cubeBuffer: FloatBuffer, textureBuffer: FloatBuffer): Texture? {
        onPreDraw()
        var hasGivenInput = false
        var fbo = firstFBO
        var input: Texture? = input
        effects.forEachIndexed { i, current ->
            var fboSet = false
            if (i == effects.size - 1) {
                renderFrameBuffer?.run { current.drawsInto(this) }
                fboSet = true
            } else {
                if (current.outputTexture == null) {
                    current.drawsInto(fbo)
                    fbo = if (fbo === firstFBO) {
                        secondFBO
                    } else {
                        firstFBO
                    }
                    fboSet = true
                }
            }
            var inputSet = false
            if (current.input == null) {
                if (!hasGivenInput) {
                    hasGivenInput = true
                    current.input = input
                    inputSet = true
                } else {
                    current.input = input
                    inputSet = true
                }
            }
            current.onDraw(cubeBuffer, textureBuffer)
            if (inputSet) {
                input = current.outputTexture
                current.input = null
            }
            if (fboSet) {
                current.drawsInto(null)
            }
        }
        return outputTexture
    }

    /**
     * {@inheritDoc}
     */
    override final fun destroy() {
        onPreDestroy()
        renderFrameBuffer?.destroy()
        firstFBO?.destroy()
        secondFBO?.destroy()
        input?.destroy()
        effects.forEach { it.destroy() }
        isInitialized = false
        onDestroy()
    }

    /**
     * {@inheritDoc}
     */
    override fun onPreDestroy() {

    }

    /**
     * {@inheritDoc}
     */
    override fun onDestroy() {

    }

    fun with(effect: Effect, func: Effect.() -> Unit) = also {
        effect.func()
        add(effect)
    }

    /**
     * Add effect to the list of effect to draw without changing its RenderInto and Input
     */
    fun add(effect: Effect) = also {
        effects.add(effect)
    }

    operator fun plusAssign(effect: Effect) {
        add(effect)
    }

    operator fun plus(effect: Effect) = GPUMultiEffect(this, effect)

    /**
     * {@inheritDoc}
     */
    override fun setOutputSize(width: Int, height: Int) = also {
        outputWidth = width
        outputHeight = height
        effects.forEach { it.setOutputSize(width, height) }
    }

    companion object {
        inline fun with(func: GPUMultiEffect.() -> Unit): GPUMultiEffect {
            val effect = GPUMultiEffect()
            effect.func()
            return effect
        }
    }
}