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

import android.opengl.GLES20
import android.support.annotation.CallSuper
import com.frontback.gpueffect.common.*
import com.frontback.gpueffect.internal.flipHorizontally
import com.frontback.gpueffect.internal.flipVertically
import java.nio.ByteBuffer
import java.nio.ByteOrder


open class TwoInputEffect<T : TwoInputEffect.Program> @JvmOverloads constructor(
        private var texture2: Texture? = null,
        program: T
) : GPUEffect<T>(program) {

    open class Program constructor(fragmentShader: String)
        : GLSLProgram(V_SHADER, fragmentShader) {
        var textureCoordinate2Attribute: Int = 0
            private set
        var inputTexture2Uniform: Int = 0
            private set

        @CallSuper
        override fun onInitialized(programId: Int) {
            textureCoordinate2Attribute = loadAttributeLocation(INPUT_TEXTURE_COORDINATE2)
            inputTexture2Uniform = loadUniformLocation(INPUT_TEXTURE2)
        }
    }

    private val texture2CoordinatesBuffer =
            ByteBuffer.allocateDirect(32)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer()

    @CallSuper
    override fun onInit() {
        texture2?.let {
            if (!it.isInitialized) {
                it.init(outputWidth, outputHeight)
            }
        }
        setRotation(Rotation.NONE, false, false)
    }

    @CallSuper
    override fun onDestroy() {
        texture2?.destroy()
    }

    @CallSuper
    override fun onPreDrawArrays() {
        texture2?.let {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE3)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, it.id)
            GLES20.glUniform1i(program.inputTexture2Uniform, 3)

            texture2CoordinatesBuffer.rewind()
            GLES20.glVertexAttribPointer(program.textureCoordinate2Attribute, 2, GLES20.GL_FLOAT, false, 0, texture2CoordinatesBuffer)
            GLES20.glEnableVertexAttribArray(program.textureCoordinate2Attribute)
        }
    }

    @CallSuper
    override fun onPostDrawArrays() {
        if (texture2 != null) {
            GLES20.glDisableVertexAttribArray(program.textureCoordinate2Attribute)
        }
    }

    open var secondInput: Texture?
        get() = texture2
        set(value) {
            texture2 = value
        }

    infix fun alsoReceives(texture: Texture?) = also {
        secondInput = texture
    }

    open fun setRotation(@Rotation rotation: Int, flipHorizontal: Boolean, flipVertical: Boolean) {
        var array = Effect.getRotation(rotation)
        if (flipHorizontal) {
            array = array.flipHorizontally()
        }
        if (flipVertical) {
            array = array.flipVertically()
        }

        texture2CoordinatesBuffer.clear()
        texture2CoordinatesBuffer.put(array)
        texture2CoordinatesBuffer.flip()
    }

    companion object {

        const val INPUT_TEXTURE2 = "inputImageTexture2"
        const val INPUT_TEXTURE_COORDINATE2 = "inputTextureCoordinate2"
        const val TEXTURE_COORDINATE2 = "textureCoordinate2"

        const val V_SHADER = """
attribute vec4 ${GLSLProgram.POSITION};
attribute vec4 ${GLSLProgram.INPUT_TEXTURE_COORDINATE};
attribute vec4 $INPUT_TEXTURE_COORDINATE2;

varying vec2 ${GLSLProgram.TEXTURE_COORDINATE};
varying vec2 $TEXTURE_COORDINATE2;

void main() {
   gl_Position = ${GLSLProgram.POSITION};
   ${GLSLProgram.TEXTURE_COORDINATE} = ${GLSLProgram.INPUT_TEXTURE_COORDINATE}.xy;
   $TEXTURE_COORDINATE2 = $INPUT_TEXTURE_COORDINATE2.xy;
}"""
    }
}