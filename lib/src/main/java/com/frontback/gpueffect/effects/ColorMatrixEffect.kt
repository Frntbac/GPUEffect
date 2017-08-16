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


open class ColorMatrixEffect @JvmOverloads constructor(
        private var _intensity: Float = 1f,
        private var _colorMatrix : FloatArray = floatArrayOf(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f)
) : GPUEffect<ColorMatrixEffect.Program>(ColorMatrixEffect.Program()) {

    class Program : GLSLProgram(fragmentShader = F_SHADER) {

        var colorMatrixLocation: Int = 0
            private set
        var intensityLocation: Int = 0
            private set

        override fun onInitialized(programId: Int) {
            colorMatrixLocation = loadUniformLocation(COLOR_MATRIX)
            intensityLocation = loadUniformLocation(INTENSITY)
        }
    }

    open var intensity : Float
        get() = _intensity
        set(value) {
            _intensity = value
            setFloat(program.intensityLocation, value)
        }

    open var colorMatrix : FloatArray
        get() = _colorMatrix
        set(value) {
            _colorMatrix = value
            setUniformMatrix4f(program.colorMatrixLocation, value)
        }

    @CallSuper
    override fun onInit() {
        intensity = intensity
        colorMatrix = colorMatrix
    }


    companion object {
        const val COLOR_MATRIX = "colorMatrix"
        const val INTENSITY = "intensity"

        const val F_SHADER = """
varying highp vec2 ${GLSLProgram.TEXTURE_COORDINATE};

uniform sampler2D ${GLSLProgram.INPUT_TEXTURE};

uniform lowp mat4 $COLOR_MATRIX;
uniform lowp float $INTENSITY;

void main() {
    lowp vec4 textureColor = texture2D(${GLSLProgram.INPUT_TEXTURE}, ${GLSLProgram.TEXTURE_COORDINATE});
    lowp vec4 outputColor = textureColor * $COLOR_MATRIX;

    gl_FragColor = ($INTENSITY * outputColor) + ((1.0 - $INTENSITY) * textureColor);
}"""
    }
}