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

package com.frontback.gpueffect.effects

import android.opengl.Matrix
import android.support.annotation.CallSuper
import com.frontback.gpueffect.common.GLSLProgram
import com.frontback.gpueffect.common.GPUEffect
import com.frontback.gpueffect.common.Texture
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer


open class TransformEffect @JvmOverloads constructor(
        // This applies the transform to the raw frame data if set to TRUE,
        // the default of NO takes the aspect ratio of the image input into account when rotating
        private var _ignoreAspectRatio: Boolean = false,
        // sets the anchor point to top left corner
        private var _anchorTopLeft: Boolean = false
) : GPUEffect<TransformEffect.Program>(TransformEffect.Program()) {

    private val adjustedVertices = FloatArray(8)
    private val vertBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder()).asFloatBuffer()
    private var _orthographicMatrix = FloatArray(16)
    private var _transform3D = FloatArray(16)

    init {
        Matrix.orthoM(_orthographicMatrix, 0, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f)
        Matrix.setIdentityM(_transform3D, 0)
    }

    class Program : GLSLProgram(vertexShader = V_SHADER) {
        var transformMatrixUniform: Int = 0
            private set
        var orthographicMatrixUniform: Int = 0
            private set

        @CallSuper
        override fun onInitialized(programId: Int) {
            transformMatrixUniform = loadUniformLocation(TRANSFORM)
            orthographicMatrixUniform = loadUniformLocation(ORTHOGRAPIC)
        }
    }

    open var transform3D: FloatArray
        get() = _transform3D
        set(value) {
            _transform3D = value
            setUniformMatrix4f(program.transformMatrixUniform, value)
        }

    open var anchorTopLeft: Boolean
        get() = _anchorTopLeft
        set(value) {
            _anchorTopLeft = value
            ignoreAspectRatio = value
        }

    open var ignoreAspectRatio: Boolean
        get() = _ignoreAspectRatio
        set(value) {
            _ignoreAspectRatio = value
            if (value) {
                Matrix.orthoM(_orthographicMatrix, 0, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f)
                orthographicMatrix = _orthographicMatrix
            } else {
                setOutputSize(outputWidth, outputHeight)
            }
        }

    open var orthographicMatrix : FloatArray
        get() = _orthographicMatrix
        set(value) {
            _orthographicMatrix = value
            setUniformMatrix4f(program.orthographicMatrixUniform, value)
        }

    @CallSuper
    override fun onInit() {
        transform3D = transform3D
        setUniformMatrix4f(program.orthographicMatrixUniform, orthographicMatrix)
        setOutputSize(outputWidth, outputHeight)
    }

    @CallSuper
    override fun setOutputSize(width: Int, height: Int) = also {
        super.setOutputSize(width, height)
        if (program.isInitialized && !ignoreAspectRatio) {
            Matrix.orthoM(_orthographicMatrix, 0, -1.0f, 1.0f, -1.0f * height.toFloat() / width.toFloat(), 1.0f * height.toFloat() / width.toFloat(), -1.0f, 1.0f)
            orthographicMatrix = _orthographicMatrix
        }
    }

    @CallSuper
    override fun onDraw(cubeBuffer: FloatBuffer, textureBuffer: FloatBuffer): Texture? {
        if (!ignoreAspectRatio) {
            cubeBuffer.rewind()
            cubeBuffer.get(adjustedVertices)
            cubeBuffer.rewind()

            val normalizedHeight = outputHeight.toFloat() / outputWidth.toFloat()
            adjustedVertices[1] *= normalizedHeight
            adjustedVertices[3] *= normalizedHeight
            adjustedVertices[5] *= normalizedHeight
            adjustedVertices[7] *= normalizedHeight

            vertBuffer.clear()
            vertBuffer.put(adjustedVertices).flip()
            return super.onDraw(vertBuffer, textureBuffer)
        }

        return super.onDraw(cubeBuffer, textureBuffer)
    }

    companion object {

        const val TRANSFORM = "transformMatrix"
        const val ORTHOGRAPIC = "orthographicMatrix"

        const val V_SHADER = "" +
                "attribute vec4 ${GLSLProgram.POSITION};\n" +
                "attribute vec4 ${GLSLProgram.INPUT_TEXTURE_COORDINATE};\n" +
                "\n" +
                "uniform mat4 $TRANSFORM;\n" +
                "uniform mat4 $ORTHOGRAPIC;\n" +
                "\n" +
                "varying vec2 ${GLSLProgram.TEXTURE_COORDINATE};\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "   gl_Position = $TRANSFORM * vec4(${GLSLProgram.POSITION}.xyz, 1.0) * $ORTHOGRAPIC;\n" +
                "   textureCoordinate = ${GLSLProgram.INPUT_TEXTURE_COORDINATE}.xy;\n" +
                "}"
    }
}