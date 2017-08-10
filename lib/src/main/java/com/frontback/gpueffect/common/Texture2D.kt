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

import android.opengl.GLES20

open class Texture2D : Texture, Initializable("Texture2D") {

    override var width: Int = -1
        protected set
    override var height: Int = -1
        protected set

    /**
     * {@inheritDoc}
     */
    override val type: Int
        get() = GLES20.GL_TEXTURE_2D

    /**
     * {@inheritDoc}
     */
    override fun init(width: Int, height: Int): Int {
        GLES20.glGetError()
        destroy()
        if (width < 1 || height < 1) {
            throw IllegalArgumentException("width and height must be > 0")
        }
        this.width = width
        this.height = height
        val textureHandle = IntArray(1)
        GLES20.glGenTextures(1, textureHandle, 0)
        id = textureHandle[0]
        //Bind texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id)
        //Define texture parameters
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)

        val error = GLES20.glGetError()
        if (error != GLES20.GL_NO_ERROR) {
            destroy()
            throw IllegalStateException("Texture initialization error : $error")
        }

        return id
    }

    /**
     * {@inheritDoc}
     */
    override fun destroy() {
        if (isInitialized) {
            GLES20.glDeleteTextures(1, intArrayOf(id), 0)
            width = -1
            height = -1
            unInitialize()
        }
    }
}
