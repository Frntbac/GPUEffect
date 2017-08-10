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
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLSurface

/*
 * Copyright (C) 2017 Social Apps BVBA
 * Copyright (C) 2012 CyberAgent
 * Copyright (C) 2010 jsemler
 *
 * Adapted from https://raw.githubusercontent.com/CyberAgent/android-gpuimage/master/library/src/jp/co/cyberagent/android/gpuimage/PixelBuffer.java
 */
class PixelBuffer(private val width: Int, private val height: Int) {

    private val egl10: EGL10 = EGLContext.getEGL() as EGL10
    private val eglDisplay = egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)
    private val eglContext: EGLContext
    private val eglSurface: EGLSurface

    private val threadOwnerName: String

    init {
        if (eglDisplay === EGL10.EGL_NO_DISPLAY) {
            throw IllegalStateException("Couldn't get display")
        }
        val version = IntArray(2)
        egl10.eglInitialize(eglDisplay, version)
        val eglConfig = getConfig()

        val attrib_list = intArrayOf(EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE)
        eglContext = egl10.eglCreateContext(eglDisplay, eglConfig, EGL10.EGL_NO_CONTEXT, attrib_list)
        checkEglError(egl10, "Error while creating context")

        val attribList = intArrayOf(EGL10.EGL_WIDTH, this.width, EGL10.EGL_HEIGHT, this.height, EGL10.EGL_NONE)
        eglSurface = egl10.eglCreatePbufferSurface(eglDisplay, eglConfig, attribList)
        checkEglError(egl10, "Error while creating surface")

        egl10.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)
        checkEglError(egl10, "Error while making surface current")

        // Record thread owner's name of OpenGL context
        threadOwnerName = Thread.currentThread().name
    }

    private fun getConfig(): EGLConfig {
        val attribList = intArrayOf(EGL10.EGL_RED_SIZE, 8, EGL10.EGL_GREEN_SIZE, 8, EGL10.EGL_BLUE_SIZE, 8, EGL10.EGL_ALPHA_SIZE, 8, EGL10.EGL_RENDERABLE_TYPE, 4, EGL10.EGL_NONE)

        // No error checking performed, minimum required code to elucidate logic
        // Expand on this logic to be more selective in choosing a configuration
        val numConfig = IntArray(1)
        egl10.eglChooseConfig(eglDisplay, attribList, null, 0, numConfig)
        val configSize = numConfig[0]
        val eglConfigs = arrayOfNulls<EGLConfig>(configSize)
        if (!egl10.eglChooseConfig(eglDisplay, attribList, eglConfigs, configSize, numConfig)) {
            throw IllegalStateException("Configuration couldn't be chosen")
        }

        return eglConfigs[0]!! // Best match is probably the first configuration
    }

    var effect: Effect? = null
        set(value) {
            field = value
            if (value != null) {
                if (Thread.currentThread().name != threadOwnerName) {
                    throw IllegalStateException("setRenderer: This thread does not own the OpenGL context.")
                }

                // Call the renderer initialization routines
                value.setOutputSize(width, height)
                if (!value.isInitialized) {
                    value.init()
                }
            }
        }

    val bitmap: Bitmap
        /**
         * Get the resulted bitmap of the filter applied on the given input
         *
         * @return resulted bitmap of the filter
         */
        get() {
            // Do we have a renderer?
            val effect = effect ?: throw IllegalStateException("Effect was not set !")
            // Does this thread own the OpenGL context?
            if (Thread.currentThread().name != threadOwnerName) {
                throw IllegalStateException("getBitmap: This thread does not own the OpenGL context.")
            }
            effect.draw()
            GLES20.glFinish()
            return convertToBitmap()
        }

    /**
     * Clean up after retrieving the Bitmap
     */
    fun destroy() {
        // Does this thread own the OpenGL context?
        if (Thread.currentThread().name != threadOwnerName) {
            throw IllegalStateException("destroy: This thread does not own the OpenGL context.")
        }
        effect?.destroy()
        egl10.eglMakeCurrent(eglDisplay, EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT)

        egl10.eglDestroySurface(eglDisplay, eglSurface)
        egl10.eglDestroyContext(eglDisplay, eglContext)
        egl10.eglTerminate(eglDisplay)
    }

    private fun convertToBitmap(): Bitmap {
        val buf = ByteBuffer.allocateDirect(width * height * 4)
        buf.order(ByteOrder.LITTLE_ENDIAN)
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buf)
        buf.rewind()
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            copyPixelsFromBuffer(buf)
        }
    }

    private fun checkEglError(egl10: EGL10, message: String) {
        var failed = false
        var error = egl10.eglGetError()
        while (error != EGL10.EGL_SUCCESS) {
            Log.e(TAG, "$message: EGL error: 0x${Integer.toHexString(error)}")
            failed = true
            error = egl10.eglGetError()
        }
        if (failed) {
            throw IllegalStateException(message)
        }
    }

    companion object {
        private const val TAG = "PixelBuffer"
        private const val EGL_CONTEXT_CLIENT_VERSION = 0x3098
    }
}
