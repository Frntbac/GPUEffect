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

/**
 * Applies an emboss effect to the image.
 *
 * Intensity ranges from 0.0 to 4.0, with 1.0 as the normal level
 */
open class EmbossEffect @JvmOverloads constructor(
        @FloatRange(from = 0.0, to = 4.0) private var _intensity: Float = 1f
) : Convolution3x3Effect<Convolution3x3Effect.Program>() {

    open var intensity: Float
        @FloatRange(from = 0.0, to = 4.0)
        get() = _intensity
        set(@FloatRange(from = 0.0, to = 4.0) value) {
            _intensity = value
            convolutionKernel = floatArrayOf(value * -2.0f, -value, 0.0f,
                    -value, 1.0f, value,
                    0.0f, value, value * 2.0f)
        }

    @CallSuper
    override fun onInit() {
        super.onInit()
        intensity = intensity
    }
}