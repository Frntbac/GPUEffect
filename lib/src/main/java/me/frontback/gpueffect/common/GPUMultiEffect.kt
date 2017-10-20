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

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * Copyright (C) 2017 Social Apps BVBA
 * Copyright (C) 2012 CyberAgent
 *
 * Adapted from https://github.com/CyberAgent/android-gpuimage/blob/master/library/src/jp/co/cyberagent/android/gpuimage/GPUImageFilterGroup.java
 */

open class GPUMultiEffect(vararg _effects: Effect) : Effect {

    val effects = mutableListOf<Effect>()

    init {
        _effects.forEach {
            add(it)
        }
    }

    override final var outputWidth: Int = 0
        /** @inheritdoc */
        get
        private set

    override final var outputHeight: Int = 0
        /** @inheritdoc */
        get
        private set

    override var isInitialized: Boolean = false
        /** @inheritdoc */
        get
        protected set

    /** @inheritdoc */
    override var input: Input? = null

    @Rotation
    private var rotation = Rotation.NONE

    /** @inheritdoc */
    override var rotationArray: FloatArray = Rotation.ARRAY_NONE

    var provider: FBOProvider? = null

    private val cubeBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(32)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer().apply { put(Effect.CUBE).flip() }
    }
    private val textureBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(32)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer().apply { put(Effect.getRotation(rotation)).flip() }
    }

    /** @inheritdoc */
    override var renderFrameBuffer: FrameBuffer? = null

    override val outputTexture: Texture?
        get() = renderFrameBuffer?.texture

    override fun init() {
        renderFrameBuffer?.run {
            if (!isInitialized) {
                init(outputWidth, outputHeight)
            }
        }
        effects.forEach { it.init() }

        input?.texture?.run {
            if (!isInitialized) {
                init(outputWidth, outputHeight)
            }
        }
        if (provider == null) {
            provider = FBOProvider()
        }
        onInit()
    }

    override fun setRotation(@Rotation rotation: Int) = also {
        super.setRotation(rotation)
        this.rotation = rotation
    }

    @Rotation
    override fun getRotation() = rotation

    /** @inheritdoc */
    override fun onInit() {

    }

    /** @inheritdoc */
    override final fun draw() = onDraw(cubeBuffer, textureBuffer)

    /** @inheritdoc */
    override fun onPreDraw() {

    }

    /** @inheritdoc */
    override fun onDraw(cubeBuffer: FloatBuffer, textureBuffer: FloatBuffer): Texture? {
        onPreDraw()
        var hasGivenInput = input == null
        var input: Texture? = input?.texture
        var previousFBO: FrameBuffer? = null
        effects.forEachIndexed { i, current ->
            if (current is GPUMultiEffect) {
                current.provider = provider
            }
            // region FBO
            var fboSet = false
            if (i == effects.size - 1) {
                renderFrameBuffer?.let { current.drawsInto(it) }
                fboSet = true
            } else if (current.outputTexture == null) {
                current drawsInto provider?.acquireFor(current)
                fboSet = true
            }
            // endregion FBO
            // region Input
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
            // endregion Input
            // region Draw
            textureBuffer.clear()
            textureBuffer.put(if (i == 0) rotationArray else current.rotationArray)
            textureBuffer.flip()
            current.onDraw(cubeBuffer, textureBuffer)
            // endregion Draw
            // region Clean-up
            if (inputSet) {
                input = current.outputTexture
                current.input = null
            }
            if (fboSet) {
                provider?.release(previousFBO)
                previousFBO = if (current.renderFrameBuffer !== renderFrameBuffer)
                    current.renderFrameBuffer
                else
                    null
                current.renderFrameBuffer = null
            }
            // endregion
            if (current is GPUMultiEffect) {
                current.provider = null
            }
        }
        return outputTexture
    }

    /** @inheritdoc */
    override final fun destroy() {
        onPreDestroy()
        renderFrameBuffer?.destroy()
        input?.destroy()
        provider?.destroy()
        effects.forEach { it.destroy() }
        isInitialized = false
        onDestroy()
    }

    /** @inheritdoc */
    override fun onPreDestroy() {

    }

    /** @inheritdoc */
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

    inline fun add(effect: Effect, func: Effect.() -> Unit) = also {
        add(effect)
        effect.func()
    }

    operator fun plusAssign(effect: Effect) {
        add(effect)
    }

    operator fun plus(effect: Effect) = GPUMultiEffect(this, effect)

    /** @inheritdoc */
    override fun setOutputSize(width: Int, height: Int) = also {
        outputWidth = width
        outputHeight = height
        effects.forEach { it.setOutputSize(width, height) }
    }

    inline operator fun invoke(func: GPUMultiEffect.() -> Unit) {
        this.func()
    }
}