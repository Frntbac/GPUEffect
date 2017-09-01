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

/**
 * Copyright (C) 2017 Social Apps BVBA
 * Copyright (C) 2012 CyberAgent
 *
 * Adapted from https://github.com/CyberAgent/android-gpuimage/blob/master/library/src/jp/co/cyberagent/android/gpuimage/GPUImageOpacityFilter.java
 */

open class OpacityEffect @JvmOverloads constructor(
        private var _opacity: Float = 1.0f
) : GPUEffect<OpacityEffect.Program>(OpacityEffect.Program()) {


    class Program : GLSLProgram(fragmentShader = F_SHADER) {
        var opacityLocation: Int = 0
            private set
        @CallSuper
        override fun onInitialized(programId: Int) {
            opacityLocation = loadUniformLocation(OPACITY)
        }
    }

    open var opacity: Float
        get() = _opacity
        set(value) {
            _opacity = value
            setFloat(program.opacityLocation, value)
        }

    @CallSuper
    override fun onInit() {
        opacity = opacity
    }

    companion object {
        const val OPACITY = "opacity"

        const val F_SHADER = """
varying highp vec2 ${GLSLProgram.TEXTURE_COORDINATE};

uniform sampler2D ${GLSLProgram.INPUT_TEXTURE};
uniform lowp float $OPACITY;

void main() {
    lowp vec4 textureColor = texture2D(${GLSLProgram.INPUT_TEXTURE}, ${GLSLProgram.TEXTURE_COORDINATE});

    gl_FragColor = vec4(textureColor.rgb, textureColor.a * $OPACITY);
}"""
    }
}