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

import android.content.Context
import android.opengl.GLES20
import android.support.annotation.CallSuper
import android.util.Log
import me.frontback.gpueffect.internal.S

/**
 * Copyright (C) 2017 Social Apps BVBA
 * Copyright (C) 2012 CyberAgent
 *
 * Part of code based on https://github.com/CyberAgent/android-gpuimage/blob/master/library/src/jp/co/cyberagent/android/gpuimage/OpenGlUtils.java
 * and https://github.com/CyberAgent/android-gpuimage/blob/master/library/src/jp/co/cyberagent/android/gpuimage/GPUImageFilter.java
 */

@Suppress("unused")
open class GLSLProgram
/**
 * Constructor to receive the shaders to be loaded
 *
 * @param vertexShader   Vertex shader
 * @param fragmentShader Fragment shader
 */
@JvmOverloads constructor(
        protected val vertexShader: String = GLSLProgram.NO_VERTEX_SHADER,
        protected val fragmentShader: String = GLSLProgram.NO_FRAGMENT_SHADER
) : Program, Initializable("Program") {

    var attribPosition: Int = -1
    var uniformTexture: Int = -1
    var attribTextureCoordinate: Int = -1

    /** @inheritdoc */
    @CallSuper
    override fun init() {
        if (isInitialized) {
            throw IllegalStateException("Program is already initialized")
        }
        onInit()
        onInitialized(id)
    }

    /**
     * Where the program is initialized
     *
     * @throws IllegalStateException if program was not initialized
     */
    @CallSuper
    protected fun onInit() {
        loadProgram()
        attribPosition = loadAttributeLocation(POSITION)
        uniformTexture = loadUniformLocation(INPUT_TEXTURE)
        attribTextureCoordinate = loadAttributeLocation(INPUT_TEXTURE_COORDINATE)
    }

    /**
     * Retrieve the uniform ID in the current program
     *
     * @param name Uniform's name
     * @return the uniform ID
     * @throws IllegalStateException if program is not initialized
     */
    @JvmOverloads
    protected fun loadUniformLocation(name: String, mandatory : Boolean = false): Int {
        val id = GLES20.glGetUniformLocation(this.id, name)
        if (id < 0) {
            if (mandatory) {
                throw IllegalStateException("uniform $name was not found")
            } else if (Log.isLoggable(TAG, Log.WARN)) {
                Log.w(TAG, "uniform $name was not found")
            }
        }
        return id
    }

    /**
     * Retrieve the attribute ID in the current program
     *
     * @param name Attribute's name
     * @return the attribute ID
     * @throws IllegalStateException if program is not initialized
     */
    @JvmOverloads
    protected fun loadAttributeLocation(name: String, mandatory : Boolean = false): Int {
        val id = GLES20.glGetAttribLocation(id, name)
        if (id < 0) {
            if (mandatory) {
                throw IllegalStateException("attribute $name was not found")
            } else if (Log.isLoggable(TAG, Log.WARN)) {
                Log.w(TAG, "attribute $name was not found")
            }
        }
        return id
    }

    /** @inheritdoc */
    @CallSuper
    override fun destroy() {
        if (isInitialized) {
            GLES20.glDeleteProgram(id)
            unInitialize()
            attribPosition = -1
            uniformTexture = -1
            attribTextureCoordinate = -1
        }
    }

    /**
     * Called when the program has been initialized
     */
    open protected fun onInitialized(@Suppress("UNUSED_PARAMETER") programId: Int) {
        //
    }

    /**
     * Called by [init] to load the program.
     *
     * @return The program ID
     * @throws IllegalStateException when the program couldn't be loaded correctly
     */
    protected fun loadProgram() {
        val link = IntArray(1)
        val iVShader = loadShader(vertexShader, GLES20.GL_VERTEX_SHADER)
        if (iVShader <= 0) {
            unInitialize()
            throw IllegalStateException("Vertex shader couldn't be loaded")
        }
        val iFShader = loadShader(fragmentShader, GLES20.GL_FRAGMENT_SHADER)
        if (iFShader <= 0) {
            unInitialize()
            throw IllegalStateException("Fragment shader couldn't be loaded")
        }

        id = GLES20.glCreateProgram()

        GLES20.glAttachShader(id, iVShader)
        GLES20.glAttachShader(id, iFShader)

        GLES20.glLinkProgram(id)

        GLES20.glGetProgramiv(id, GLES20.GL_LINK_STATUS, link, 0)
        if (link[0] <= 0) {
            unInitialize()
            throw IllegalStateException("GLSLProgram linking failed")
        }
        GLES20.glDeleteShader(iVShader)
        GLES20.glDeleteShader(iFShader)
    }

    protected fun loadShader(strSource: String, iType: Int): Int {
        val compiled = IntArray(1)
        val iShader = GLES20.glCreateShader(iType)
        GLES20.glShaderSource(iShader, strSource)
        GLES20.glCompileShader(iShader)
        GLES20.glGetShaderiv(iShader, GLES20.GL_COMPILE_STATUS, compiled, 0)
        if (compiled[0] <= 0) {
            unInitialize()
            throw IllegalStateException("Load Shader Failed with Compilation error :\n ${GLES20.glGetShaderInfoLog(iShader)}")
        }
        return iShader
    }

    companion object {

        const val TAG = "GLSLProgram"

        const val INPUT_TEXTURE = "inputImageTexture"
        const val INPUT_TEXTURE_COORDINATE = "inputTextureCoordinate"
        const val TEXTURE_COORDINATE = "textureCoordinate"
        const val POSITION = "position"

        const val NO_VERTEX_SHADER = """
attribute vec4 $POSITION;
attribute vec4 $INPUT_TEXTURE_COORDINATE;

varying vec2 $TEXTURE_COORDINATE;

void main() {
    gl_Position = $POSITION;
    $TEXTURE_COORDINATE = $INPUT_TEXTURE_COORDINATE.xy;
}"""

        const val NO_FRAGMENT_SHADER = """
varying highp vec2 $TEXTURE_COORDINATE;

uniform sampler2D $INPUT_TEXTURE;

void main() {
     gl_FragColor = texture2D($INPUT_TEXTURE, $TEXTURE_COORDINATE);
}"""

        /**
         * Retrieve the shader's string from file in assets
         *
         * @param file    Name of the file in the assets folder
         * @param context Android context
         * @return Shader's string
         * @throws RuntimeException if something went wrong
         */
        @JvmStatic
        fun loadShader(context: Context, file: String): String {
            try {
                val assetManager = context.assets
                val ims = assetManager.open(file)

                val re = ims.S
                ims.close()
                return re
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

        }
    }
}
