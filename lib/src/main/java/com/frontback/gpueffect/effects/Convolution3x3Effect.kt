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

open class Convolution3x3Effect<T : Convolution3x3Effect.Program> @JvmOverloads constructor(
        private var _convolutionKernel: FloatArray = floatArrayOf(
                0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f
        ),
        @Suppress("UNCHECKED_CAST") program: T = Convolution3x3Effect.Program() as T
) : TextureSampling3x3Effect<T>(program = program) {

    open class Program(fragmentShader: String = F_SHADER) : TextureSampling3x3Effect.Program(fragmentShader = fragmentShader) {
        var uniformConvolutionMatrix = 0
            private set
        @CallSuper
        override fun onInitialized(programId: Int) {
            super.onInitialized(programId)
            uniformConvolutionMatrix = loadUniformLocation(CONVOLUTION_MATRIX)
        }
    }

    open var convolutionKernel: FloatArray
        get() = _convolutionKernel
        set(value) {
            _convolutionKernel = value
            setUniformMatrix3f(program.uniformConvolutionMatrix, value)
        }

    @CallSuper
    override fun onInit() {
        super.onInit()
        convolutionKernel = convolutionKernel
    }

    companion object {
        const val CONVOLUTION_MATRIX = "convolutionMatrix"

        const val F_SHADER = "" +
                "precision highp float;\n" +
                "\n" +
                "uniform sampler2D ${GLSLProgram.INPUT_TEXTURE};\n" +
                "\n" +
                "uniform mediump mat3 $CONVOLUTION_MATRIX;\n" +
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
                "    mediump vec4 bottomColor = texture2D(${GLSLProgram.INPUT_TEXTURE}, $BOTTOM);\n" +
                "    mediump vec4 bottomLeftColor = texture2D(${GLSLProgram.INPUT_TEXTURE}, $BOTTOM_LEFT);\n" +
                "    mediump vec4 bottomRightColor = texture2D(${GLSLProgram.INPUT_TEXTURE}, $BOTTOM_RIGHT);\n" +
                "    mediump vec4 centerColor = texture2D(${GLSLProgram.INPUT_TEXTURE}, ${GLSLProgram.TEXTURE_COORDINATE});\n" +
                "    mediump vec4 leftColor = texture2D(${GLSLProgram.INPUT_TEXTURE}, $LEFT);\n" +
                "    mediump vec4 rightColor = texture2D(${GLSLProgram.INPUT_TEXTURE}, $RIGHT);\n" +
                "    mediump vec4 topColor = texture2D(${GLSLProgram.INPUT_TEXTURE}, $TOP);\n" +
                "    mediump vec4 topRightColor = texture2D(${GLSLProgram.INPUT_TEXTURE}, $TOP_RIGHT);\n" +
                "    mediump vec4 topLeftColor = texture2D(${GLSLProgram.INPUT_TEXTURE}, $TOP_LEFT);\n" +
                "\n" +
                "    mediump vec4 resultColor = topLeftColor * $CONVOLUTION_MATRIX[0][0] + topColor * $CONVOLUTION_MATRIX[0][1] + topRightColor * $CONVOLUTION_MATRIX[0][2];\n" +
                "    resultColor += leftColor * $CONVOLUTION_MATRIX[1][0] + centerColor * $CONVOLUTION_MATRIX[1][1] + rightColor * $CONVOLUTION_MATRIX[1][2];\n" +
                "    resultColor += bottomLeftColor * $CONVOLUTION_MATRIX[2][0] + bottomColor * $CONVOLUTION_MATRIX[2][1] + bottomRightColor * $CONVOLUTION_MATRIX[2][2];\n" +
                "\n" +
                "    gl_FragColor = resultColor;\n" +
                "}"
    }
}