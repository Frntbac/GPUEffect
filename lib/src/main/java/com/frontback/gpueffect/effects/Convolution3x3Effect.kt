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

        const val F_SHADER = """
precision highp float;

uniform sampler2D ${GLSLProgram.INPUT_TEXTURE};

uniform mediump mat3 $CONVOLUTION_MATRIX;

varying vec2 ${GLSLProgram.TEXTURE_COORDINATE};
varying vec2 $LEFT;
varying vec2 $RIGHT;

varying vec2 $TOP;
varying vec2 $TOP_LEFT;
varying vec2 $TOP_RIGHT;

varying vec2 $BOTTOM;
varying vec2 $BOTTOM_LEFT;
varying vec2 $BOTTOM_RIGHT;

void main() {
    mediump vec4 bottomColor = texture2D(${GLSLProgram.INPUT_TEXTURE}, $BOTTOM);
    mediump vec4 bottomLeftColor = texture2D(${GLSLProgram.INPUT_TEXTURE}, $BOTTOM_LEFT);
    mediump vec4 bottomRightColor = texture2D(${GLSLProgram.INPUT_TEXTURE}, $BOTTOM_RIGHT);
    mediump vec4 centerColor = texture2D(${GLSLProgram.INPUT_TEXTURE}, ${GLSLProgram.TEXTURE_COORDINATE});
    mediump vec4 leftColor = texture2D(${GLSLProgram.INPUT_TEXTURE}, $LEFT);
    mediump vec4 rightColor = texture2D(${GLSLProgram.INPUT_TEXTURE}, $RIGHT);
    mediump vec4 topColor = texture2D(${GLSLProgram.INPUT_TEXTURE}, $TOP);
    mediump vec4 topRightColor = texture2D(${GLSLProgram.INPUT_TEXTURE}, $TOP_RIGHT);
    mediump vec4 topLeftColor = texture2D(${GLSLProgram.INPUT_TEXTURE}, $TOP_LEFT);

    mediump vec4 resultColor = topLeftColor * $CONVOLUTION_MATRIX[0][0] + topColor * $CONVOLUTION_MATRIX[0][1] + topRightColor * $CONVOLUTION_MATRIX[0][2];
    resultColor += leftColor * $CONVOLUTION_MATRIX[1][0] + centerColor * $CONVOLUTION_MATRIX[1][1] + rightColor * $CONVOLUTION_MATRIX[1][2];
    resultColor += bottomLeftColor * $CONVOLUTION_MATRIX[2][0] + bottomColor * $CONVOLUTION_MATRIX[2][1] + bottomRightColor * $CONVOLUTION_MATRIX[2][2];

    gl_FragColor = resultColor;
}"""
    }
}