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


open class BrightnessEffect @JvmOverloads constructor(
        private var _brightness: Float = 0.0f
) : GPUEffect<BrightnessEffect.Program>(BrightnessEffect.Program()) {

    class Program() : GLSLProgram(fragmentShader = F_SHADER) {
        var brightnessLocation: Int = 0
            private set

        @CallSuper
        override fun onInitialized(programId: Int) {
            brightnessLocation = loadUniformLocation(BRIGHTNESS)
        }
    }

    var brightness: Float
        get() = _brightness
        set(value) {
            _brightness = value
            setFloat(program.brightnessLocation, value)
        }

    @CallSuper
    override fun onInit() {
        brightness = brightness
    }

    companion object {

        const val BRIGHTNESS = "brightness"

        const val F_SHADER = """
varying highp vec2 ${GLSLProgram.TEXTURE_COORDINATE};

uniform sampler2D ${GLSLProgram.INPUT_TEXTURE};
uniform lowp float $BRIGHTNESS;

void main() {
   lowp vec4 textureColor = texture2D(${GLSLProgram.INPUT_TEXTURE}, ${GLSLProgram.TEXTURE_COORDINATE});

   gl_FragColor = vec4((textureColor.rgb + vec3($BRIGHTNESS)), textureColor.w);
}"""
    }
}