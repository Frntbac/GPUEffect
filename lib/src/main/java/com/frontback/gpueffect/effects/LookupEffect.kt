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
import com.frontback.gpueffect.common.Texture

open class LookupEffect @JvmOverloads constructor(
        texture2: Texture? = null,
        private var _intensity: Float = 1f
) : TwoInputEffect<LookupEffect.Program>(texture2, LookupEffect.Program()) {

    class Program : TwoInputEffect.Program(fragmentShader = F_SHADER) {

        var intensityLocation: Int = 0
            private set

        @CallSuper
        override fun onInitialized(programId: Int) {
            super.onInitialized(programId)
            intensityLocation = loadUniformLocation(INTENSITY)
        }
    }

    open var intensity: Float
        get() = _intensity
        set(value) {
            _intensity = value
            setFloat(program.intensityLocation, value)
        }

    override fun onInit() {
        super.onInit()
        intensity = intensity
    }


    companion object {
        const val INTENSITY = "intensity"

        const val F_SHADER = "" +
                "varying highp vec2 ${GLSLProgram.TEXTURE_COORDINATE};\n" +
                "varying highp vec2 $TEXTURE_COORDINATE2;\n" +
                "\n" +
                "uniform sampler2D ${GLSLProgram.INPUT_TEXTURE};\n" +
                "uniform sampler2D $INPUT_TEXTURE2; // lookup texture\n" +
                "\n" +
                "uniform lowp float $INTENSITY;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "   highp vec4 textureColor = texture2D(${GLSLProgram.INPUT_TEXTURE}, ${GLSLProgram.TEXTURE_COORDINATE});\n" +
                "\n" +
                "   highp float blueColor = textureColor.b * 63.0;\n" +
                "\n" +
                "   highp vec2 quad1;\n" +
                "   quad1.y = floor(floor(blueColor) / 8.0);\n" +
                "   quad1.x = floor(blueColor) - (quad1.y * 8.0);\n" +
                "\n" +
                "   highp vec2 quad2;\n" +
                "   quad2.y = floor(ceil(blueColor) / 8.0);\n" +
                "   quad2.x = ceil(blueColor) - (quad2.y * 8.0);\n" +
                "\n" +
                "   highp vec2 texPos1;\n" +
                "   texPos1.x = (quad1.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.r);\n" +
                "   texPos1.y = (quad1.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.g);\n" +
                "\n" +
                "   highp vec2 texPos2;\n" +
                "   texPos2.x = (quad2.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.r);\n" +
                "   texPos2.y = (quad2.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.g);\n" +
                "\n" +
                "   lowp vec4 newColor1 = texture2D($INPUT_TEXTURE2, texPos1);\n" +
                "   lowp vec4 newColor2 = texture2D($INPUT_TEXTURE2, texPos2);\n" +
                "\n" +
                "   lowp vec4 newColor = mix(newColor1, newColor2, fract(blueColor));\n" +
                "   gl_FragColor = mix(textureColor, vec4(newColor.rgb, textureColor.w), $INTENSITY);\n" +
                "}"
    }
}