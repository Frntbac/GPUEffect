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

class FBOTexture(override val texture: Texture = Texture2D()) : FrameBuffer, Initializable("FBO") {

    /**
     * {@inheritDoc}
     */
    override var width: Int = 0
        private set
    /**
     * {@inheritDoc}
     */
    override var height: Int = 0
        private set

    /**
     * {@inheritDoc}
     */
    override fun init(width: Int, height: Int): Int {
        destroy()
        if (width < 1 || height < 1) {
            throw IllegalArgumentException("width and height must be > 0")
        }
        val temp = IntArray(1)
        GLES20.glGenFramebuffers(1, temp, 0)
        id = temp[0]
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, id)
        texture.init(width, height)
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, texture.id, 0)

        val status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)

        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            throw IllegalStateException("There was an error generating the frame buffer, error = ${GLES20.glGetError()}")
        }

        this.width = width
        this.height = height

        return id
    }

    /**
     * {@inheritDoc}
     */
    override fun destroy() {
        if (isInitialized) {
            GLES20.glDeleteFramebuffers(1, intArrayOf(id), 0)
            unInitialize()
        }
    }
}
