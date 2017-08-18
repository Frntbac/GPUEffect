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

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils

/**
 * Class used to bind a bitmap into a texture
 */
open class BitmapTexture(
        /**
         * Get the attached bitmap
         *
         * @return the attached bitmap
         */
        private val bitmap: Bitmap) : Texture, Initializable("BitmapTexture") {

    override val width: Int = bitmap.width
    override val height: Int = bitmap.height

    /** @inheritdoc */
    override val type: Int
        get() = GLES20.GL_TEXTURE_2D

    /** @inheritdoc */
    override fun init(width: Int, height: Int): Int {
        GLES20.glGetError()
        destroy()

        val textureHandle = IntArray(1)
        GLES20.glGenTextures(1, textureHandle, 0)
        id = textureHandle[0]
        if (id == 0) {
            unInitialize()
            throw IllegalStateException("Texture couldn't be generated")
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

        val error = GLES20.glGetError()
        if (error != GLES20.GL_NO_ERROR) {
            destroy()
            throw IllegalStateException("Bitmap couldn't be loaded into texture : " + error)
        }
        return id
    }

    /** @inheritdoc */
    override fun destroy() {
        if (isInitialized) {
            GLES20.glDeleteTextures(1, intArrayOf(id), 0)
            unInitialize()
        }
    }
}
