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

import com.frontback.gpueffect.common.GLSLProgram
import com.frontback.gpueffect.common.Texture


open class MultiplyBlendEffect @JvmOverloads constructor(
    texture2 : Texture? = null
) : TwoInputEffect<TwoInputEffect.Program>(texture2, TwoInputEffect.Program(F_SHADER)) {

    companion object {

        const val F_SHADER = "" +
                "varying highp vec2 ${GLSLProgram.TEXTURE_COORDINATE};\n" +
                "varying highp vec2 $TEXTURE_COORDINATE2;\n" +
                "\n" +
                "uniform sampler2D ${GLSLProgram.INPUT_TEXTURE};\n" +
                "uniform sampler2D $INPUT_TEXTURE2;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "   lowp vec4 base = texture2D(${GLSLProgram.INPUT_TEXTURE}, ${GLSLProgram.TEXTURE_COORDINATE});\n" +
                "   lowp vec4 overlayer = texture2D($INPUT_TEXTURE2, $TEXTURE_COORDINATE2);\n" +
                "\n" +
                "   gl_FragColor = overlayer * base + overlayer * (1.0 - base.a) + base * (1.0 - overlayer.a);\n" +
                " }"
    }
}