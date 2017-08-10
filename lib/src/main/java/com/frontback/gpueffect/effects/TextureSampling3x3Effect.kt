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

import android.support.annotation.CallSuper
import com.frontback.gpueffect.common.GLSLProgram
import com.frontback.gpueffect.common.GPUEffect


open class TextureSampling3x3Effect<T : TextureSampling3x3Effect.Program> @JvmOverloads constructor(
        private var _texelWidth: Float = 0f,
        private var _texelHeight: Float = 0f,
        private var _lineSize: Float = 1f,
        program: T
) : GPUEffect<T>(program) {

    open class Program(fragmentShader: String) : GLSLProgram(V_SHADER, fragmentShader) {
        var texelWidthLocation = 0
            private set
        var texelHeightLocation = 0
            private set

        @CallSuper
        override fun onInitialized(programId: Int) {
            texelWidthLocation = loadUniformLocation(TEXEL_WIDTH)
            texelHeightLocation = loadUniformLocation(TEXEL_HEIGHT)
        }
    }

    private var hasOverriddenImageSizeFactor = false

    open var texelWidth: Float
        get() = _texelWidth
        set(value) {
            _texelWidth = value
            hasOverriddenImageSizeFactor = true
            setFloat(program.texelWidthLocation, value)
        }

    open var texelHeight: Float
        get() = _texelHeight
        set(value) {
            _texelHeight = value
            hasOverriddenImageSizeFactor = true
            setFloat(program.texelHeightLocation, value)
        }

    open var lineSize: Float
        get() = _lineSize
        set(value) {
            _lineSize = value
            texelWidth = value / outputWidth
            texelHeight = value / outputHeight
            updateTexelValues()
        }

    @CallSuper
    override fun onInit() {
        if (texelWidth != 0f) {
            updateTexelValues()
        }
        setOutputSize(outputWidth, outputHeight)
    }

    @CallSuper
    override fun setOutputSize(width: Int, height: Int) = also {
        super.setOutputSize(width, height)
        if (program.isInitialized && !hasOverriddenImageSizeFactor) {
            lineSize = lineSize
        }
    }


    open fun updateTexelValues() {
        texelWidth = texelWidth
        texelHeight = texelHeight
    }

    companion object {
        const val TEXEL_WIDTH = "texelWidth"
        const val TEXEL_HEIGHT = "texelHeight"

        const val LEFT = "leftTextureCoordinate"
        const val RIGHT = "rightTextureCoordinate"
        const val TOP = "topTextureCoordinate"
        const val TOP_LEFT = "topLeftTextureCoordinate"
        const val TOP_RIGHT = "topRightTextureCoordinate"
        const val BOTTOM = "bottomTextureCoordinate"
        const val BOTTOM_LEFT = "bottomLeftTextureCoordinate"
        const val BOTTOM_RIGHT = "bottomRightTextureCoordinate"

        const val V_SHADER = "" +
                "attribute vec4 ${GLSLProgram.POSITION};\n" +
                "attribute vec4 ${GLSLProgram.INPUT_TEXTURE_COORDINATE};\n" +
                "\n" +
                "uniform highp float $TEXEL_WIDTH; \n" +
                "uniform highp float $TEXEL_HEIGHT; \n" +
                "\n" +
                "varying vec2 ${GLSLProgram.TEXTURE_COORDINATE};\n" +
                "varying vec2 $LEFT;\n" +
                "varying vec2 $RIGHT;\n" +
                "\n" +
                "varying vec2 $TOP;\n" +
                "varying vec2 $TOP_LEFT;\n" +
                "varying vec2 $TOP_RIGHT;\n" +
                "\n" +
                "varying vec2 $BOTTOM;\n" +
                "varying vec2 $BOTTOM_LEFT;\n" +
                "varying vec2 $BOTTOM_RIGHT;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "   gl_Position = ${GLSLProgram.POSITION};\n" +
                "\n" +
                "   vec2 widthStep = vec2($TEXEL_WIDTH, 0.0);\n" +
                "   vec2 heightStep = vec2(0.0, $TEXEL_HEIGHT);\n" +
                "   vec2 widthHeightStep = vec2($TEXEL_WIDTH, $TEXEL_HEIGHT);\n" +
                "   vec2 widthNegativeHeightStep = vec2($TEXEL_WIDTH, -$TEXEL_HEIGHT);\n" +
                "\n" +
                "   ${GLSLProgram.TEXTURE_COORDINATE} = ${GLSLProgram.INPUT_TEXTURE_COORDINATE}.xy;\n" +
                "   $LEFT = ${GLSLProgram.INPUT_TEXTURE_COORDINATE}.xy - widthStep;\n" +
                "   $RIGHT = ${GLSLProgram.INPUT_TEXTURE_COORDINATE}.xy + widthStep;\n" +
                "\n" +
                "   $TOP = ${GLSLProgram.INPUT_TEXTURE_COORDINATE}.xy - heightStep;\n" +
                "   $TOP_LEFT = ${GLSLProgram.INPUT_TEXTURE_COORDINATE}.xy - widthHeightStep;\n" +
                "   $TOP_RIGHT = ${GLSLProgram.INPUT_TEXTURE_COORDINATE}.xy + widthNegativeHeightStep;\n" +
                "\n" +
                "   $BOTTOM = ${GLSLProgram.INPUT_TEXTURE_COORDINATE}.xy + heightStep;\n" +
                "   $BOTTOM_LEFT = ${GLSLProgram.INPUT_TEXTURE_COORDINATE}.xy - widthNegativeHeightStep;\n" +
                "   $BOTTOM_RIGHT = ${GLSLProgram.INPUT_TEXTURE_COORDINATE}.xy + widthHeightStep;\n" +
                "}"
    }
}