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

package me.frontback.gpueffect.effects

import android.support.annotation.CallSuper
import me.frontback.gpueffect.common.GLSLProgram
import me.frontback.gpueffect.common.Texture

/**
 * Copyright (C) 2017 Social Apps BVBA
 * Copyright (C) 2012 CyberAgent
 *
 * Adapted from https://github.com/CyberAgent/android-gpuimage/blob/master/library/src/jp/co/cyberagent/android/gpuimage/GPUImageLookupFilter.java
 */

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

        const val F_SHADER = """
varying highp vec2 ${GLSLProgram.TEXTURE_COORDINATE};
varying highp vec2 $TEXTURE_COORDINATE2;

uniform sampler2D ${GLSLProgram.INPUT_TEXTURE};
uniform sampler2D $INPUT_TEXTURE2; // lookup texture

uniform lowp float $INTENSITY;

void main() {
   highp vec4 textureColor = texture2D(${GLSLProgram.INPUT_TEXTURE}, ${GLSLProgram.TEXTURE_COORDINATE});

   highp float blueColor = textureColor.b * 63.0;

   highp vec2 quad1;
   quad1.y = floor(floor(blueColor) / 8.0);
   quad1.x = floor(blueColor) - (quad1.y * 8.0);

   highp vec2 quad2;
   quad2.y = floor(ceil(blueColor) / 8.0);
   quad2.x = ceil(blueColor) - (quad2.y * 8.0);

   highp vec2 texPos1;
   texPos1.x = (quad1.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.r);
   texPos1.y = (quad1.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.g);

   highp vec2 texPos2;
   texPos2.x = (quad2.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.r);
   texPos2.y = (quad2.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.g);

   lowp vec4 newColor1 = texture2D($INPUT_TEXTURE2, texPos1);
   lowp vec4 newColor2 = texture2D($INPUT_TEXTURE2, texPos2);

   lowp vec4 newColor = mix(newColor1, newColor2, fract(blueColor));
   gl_FragColor = mix(textureColor, vec4(newColor.rgb, textureColor.w), $INTENSITY);
}"""
    }
}