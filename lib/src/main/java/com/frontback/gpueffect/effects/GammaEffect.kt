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
import android.support.annotation.FloatRange
import com.frontback.gpueffect.common.GLSLProgram
import com.frontback.gpueffect.common.GPUEffect

/**
 * gamma value ranges from 0.0 to 3.0, with 1.0 as the normal level
 */
open class GammaEffect @JvmOverloads constructor(
        @FloatRange(from = 0.0, to = 3.0) private var _gamma: Float = 1.2f
) : GPUEffect<GammaEffect.Program>(GammaEffect.Program()) {


    class Program : GLSLProgram(fragmentShader = F_SHADER) {

        var gammaLocation: Int = 0
            private set
        @CallSuper
        override fun onInitialized(programId: Int) {
            gammaLocation = loadUniformLocation(GAMMA)
        }
    }

    open var gamma : Float
        get() = _gamma
        set(value) {
            _gamma = value
            setFloat(program.gammaLocation, value)
        }

    @CallSuper
    override fun onInit() {
        gamma = gamma
    }

    companion object {
        const val GAMMA = "gamma"

        const val F_SHADER = "" +
                "varying vec2 ${GLSLProgram.TEXTURE_COORDINATE};\n" +
                "" +
                "uniform sampler2D ${GLSLProgram.INPUT_TEXTURE};\n" +
                "uniform lowp float $GAMMA;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "   lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                "   gl_FragColor = vec4(pow(textureColor.rgb, vec3($GAMMA)), textureColor.w);\n" +
                "}"
    }

}