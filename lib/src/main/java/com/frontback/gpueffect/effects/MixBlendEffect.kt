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
import com.frontback.gpueffect.common.Texture

open class MixBlendEffect<T : MixBlendEffect.Program>(
        texture2: Texture? = null,
        @FloatRange(from = 0.0, to = 1.0) private var _mix: Float = 0.5f,
        @Suppress("UNCHECKED_CAST") program: T = MixBlendEffect.Program() as T
) : TwoInputEffect<T>(program = program, texture2 =  texture2) {

    open class Program @JvmOverloads constructor(fragmentShader: String = GLSLProgram.NO_FRAGMENT_SHADER)
        : TwoInputEffect.Program(fragmentShader) {
        var mixLocation: Int = 0
            private set

        @CallSuper
        override fun onInitialized(programId: Int) {
            super.onInitialized(programId)
            mixLocation = loadUniformLocation(MIXTURE_PERCENT)
        }
    }

    open var mix: Float
        get() = _mix
        set(value) {
            _mix = value
            setFloat(program.mixLocation, value)
        }

    @CallSuper
    override fun onInit() {
        super.onInit()
        mix = mix
    }

    companion object {
        const val MIXTURE_PERCENT = "mixturePercent"
    }
}