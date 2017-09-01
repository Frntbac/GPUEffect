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

import android.graphics.PointF
import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.util.*

/**
 * Copyright (C) 2017 Social Apps BVBA
 * Copyright (C) 2012 CyberAgent
 *
 * Adapted from https://github.com/CyberAgent/android-gpuimage/blob/master/library/src/jp/co/cyberagent/android/gpuimage/GPUImageFilter.java
 */

@Suppress("unused")
open class GPUEffect<T : GLSLProgram> @JvmOverloads constructor(
        @Suppress("UNCHECKED_CAST") val program: T = GLSLProgram() as T
) : Effect {

    private val pendingTasks = LinkedList<Runnable>()

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

    @Rotation
    private var rotation = Rotation.NONE

    /** @inheritdoc */
    override var rotationArray = Rotation.ARRAY_NONE

    /** @inheritdoc */
    override var isInitialized = false
        protected set

    /** @inheritdoc */
    override var renderFrameBuffer: FrameBuffer? = null

    override final var outputWidth: Int = 0
        /** @inheritdoc */
        get
        private set
    override final var outputHeight: Int = 0
        /** @inheritdoc */
        get
        private set

    /** @inheritdoc */
    override var input: Input? = null

    /** @inheritdoc */
    override final fun init() {
        if (!program.isInitialized) {
            program.init()
        }
        input?.texture?.let {
            if (!it.isInitialized) {
                it.init(outputWidth, outputHeight)
            }
        }
        renderFrameBuffer?.let {
            if (!it.isInitialized) {
                it.init(outputWidth, outputHeight)
            }
        }
        isInitialized = true
        onInit()
    }

    /** @inheritdoc */
    override fun onInit() {

    }

    /** @inheritdoc */
    override final fun draw(): Texture? {
        textureBuffer.clear()
        textureBuffer.put(rotationArray)
        textureBuffer.flip()
        return onDraw(cubeBuffer, textureBuffer)
    }

    /** @inheritdoc */
    override fun onDraw(cubeBuffer: FloatBuffer, textureBuffer: FloatBuffer): Texture? {
        val inputTexture = input?.texture
        if (!isInitialized || inputTexture == null) {
            return inputTexture
        }

        renderFrameBuffer?.run {
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, id)
        }

        onPreDraw()
        if (!inputTexture.isInitialized) {
            inputTexture.init(outputWidth, outputHeight)
        }

        GLES20.glClearColor(0f, 0f, 0f, 0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        GLES20.glUseProgram(program.id)
        GLES20.glViewport(0, 0, outputWidth, outputHeight)
        runPendingOnDrawTasks()

        if (program.attribPosition > -1) {
            cubeBuffer.rewind()
            GLES20.glVertexAttribPointer(program.attribPosition, 2, GLES20.GL_FLOAT, false, 0, cubeBuffer)
            GLES20.glEnableVertexAttribArray(program.attribPosition)
        }
        if (program.attribTextureCoordinate > -1) {
            textureBuffer.rewind()
            GLES20.glVertexAttribPointer(program.attribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0,
                    textureBuffer)
            GLES20.glEnableVertexAttribArray(program.attribTextureCoordinate)
        }
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(inputTexture.type, inputTexture.id)
        GLES20.glUniform1i(program.uniformTexture, 0)

        onPreDrawArrays()
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        if (program.attribPosition > -1) {
            GLES20.glDisableVertexAttribArray(program.attribPosition)
        }
        if (program.attribTextureCoordinate > -1) {
            GLES20.glDisableVertexAttribArray(program.attribTextureCoordinate)
        }
        onPostDrawArrays()
        GLES20.glFlush()
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        return outputTexture
    }

    /** @inheritdoc */
    override fun onPreDraw() {

    }

    open protected fun onPreDrawArrays() {

    }

    open protected fun onPostDrawArrays() {

    }

    open protected fun runPendingOnDrawTasks() {
        synchronized(pendingTasks) {
            while (!pendingTasks.isEmpty()) {
                pendingTasks.removeFirst().run()
            }
        }
    }

    /** @inheritdoc */
    override final fun destroy() {
        onPreDestroy()
        renderFrameBuffer?.destroy()
        program.destroy()
        input?.destroy()
        isInitialized = false
        onDestroy()
    }

    /** @inheritdoc */
    override fun onPreDestroy() {

    }

    /** @inheritdoc */
    override fun onDestroy() {

    }

    /** @inheritdoc */
    override val outputTexture: Texture?
        get() = renderFrameBuffer?.texture

    /**
     * @throws IllegalArgumentException
     */
    override final fun setRotation(@Rotation rotation: Int) = also {
        super.setRotation(rotation)
        invertWidthAndHeightIfNeeded(rotation)
        this.rotation = rotation
    }

    @Rotation
    override fun getRotation() = rotation

    private fun invertWidthAndHeightIfNeeded(@Rotation rotation: Int) {
        if (this.rotation == Rotation.NONE || this.rotation == Rotation._180 ||
                this.rotation == Rotation.UPSIDE_DOWN || this.rotation == Rotation.UPSIDE_DOWN_180) {
            if (rotation == Rotation._90 || rotation == Rotation._270 ||
                    rotation == Rotation.UPSIDE_DOWN_90 || rotation == Rotation.UPSIDE_DOWN_270) {
                val tmp = outputWidth
                outputWidth = outputHeight
                outputHeight = tmp
            }
        } else {
            if (rotation in intArrayOf(Rotation.NONE, Rotation._180,
                    Rotation.UPSIDE_DOWN, Rotation.UPSIDE_DOWN_180)) {
                val tmp = outputWidth
                outputWidth = outputHeight
                outputHeight = tmp
            }
        }
    }

    operator fun plus(effect: Effect) = GPUMultiEffect(this, effect)

    fun setInteger(location: Int, intValue: Int) {
        runOnDraw(Runnable { GLES20.glUniform1i(location, intValue) })
    }

    fun setFloat(location: Int, floatValue: Float) {
        runOnDraw(Runnable { GLES20.glUniform1f(location, floatValue) })
    }

    fun setFloatVec2(location: Int, arrayValue: FloatArray) {
        runOnDraw(Runnable { GLES20.glUniform2fv(location, 1, FloatBuffer.wrap(arrayValue)) })
    }

    fun setFloatVec3(location: Int, arrayValue: FloatArray) {
        runOnDraw(Runnable { GLES20.glUniform3fv(location, 1, FloatBuffer.wrap(arrayValue)) })
    }

    fun setFloatVec4(location: Int, arrayValue: FloatArray) {
        runOnDraw(Runnable { GLES20.glUniform4fv(location, 1, FloatBuffer.wrap(arrayValue)) })
    }

    fun setFloatArray(location: Int, arrayValue: FloatArray) {
        runOnDraw(Runnable { GLES20.glUniform1fv(location, arrayValue.size, FloatBuffer.wrap(arrayValue)) })
    }

    fun setPoint(location: Int, point: PointF) {
        setPoint(location, point.x, point.y)
    }

    fun setPoint(location: Int, x: Float, y: Float) {
        runOnDraw(Runnable {
            val vec2 = FloatArray(2)
            vec2[0] = x
            vec2[1] = y
            GLES20.glUniform2fv(location, 1, vec2, 0)
        })
    }

    fun setUniformMatrix3f(location: Int, matrix: FloatArray) {
        runOnDraw(Runnable { GLES20.glUniformMatrix3fv(location, 1, false, matrix, 0) })
    }

    fun setUniformMatrix4f(location: Int, matrix: FloatArray) {
        runOnDraw(Runnable { GLES20.glUniformMatrix4fv(location, 1, false, matrix, 0) })
    }

    open protected fun runOnDraw(runnable: Runnable) {
        synchronized(pendingTasks) {
            pendingTasks.addLast(runnable)
        }
    }

    /** @inheritdoc */
    override fun setOutputSize(width: Int, height: Int) = also {
        if (Effect.shouldInvertWidthAndHeight(rotation)) {
            outputWidth = height
            outputHeight = width
        } else {
            outputWidth = width
            outputHeight = height
        }
    }

    companion object {
        @JvmField
        val ON_SCREEN : FrameBuffer? = null
    }
}
