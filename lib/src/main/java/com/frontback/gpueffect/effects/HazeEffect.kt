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
 * Copyright (C) 2017 Social Apps BVBA
 * Copyright (C) 2012 CyberAgent
 *
 * Adapted from https://github.com/CyberAgent/android-gpuimage/blob/master/library/src/jp/co/cyberagent/android/gpuimage/GPUImageHazeFilter.java
 */

/**
 * The haze filter can be used to add or remove haze.
 *
 * This is similar to a UV filter.
 */
open class HazeEffect @JvmOverloads constructor(
        @FloatRange(from = -0.3, to = 0.3) private var _distance: Float = 0.2f,
        @FloatRange(from = -0.3, to = 0.3) private var _slope: Float = 0f
) : GPUEffect<HazeEffect.Program>(HazeEffect.Program()) {


    class Program : GLSLProgram(fragmentShader = F_SHADER) {

        var distanceLocation: Int = 0
            private set
        var slopeLocation: Int = 0
            private set

        @CallSuper
        override fun onInitialized(programId: Int) {
            distanceLocation = loadUniformLocation(DISTANCE)
            slopeLocation = loadUniformLocation(SLOPE)
        }
    }

    open var distance: Float
        get() = _distance
        /**
         * Strength of the color applied. Default 0. Values between -.3 and .3 are best.

         * @param value -0.3 to 0.3 are best, default 0
         */
        set(@FloatRange(from = -0.3, to = 0.3) value) {
            _distance = value
            setFloat(program.distanceLocation, value)
        }

    open var slope: Float
        get() = _slope
        /**
         * Amount of color change. Default 0. Values between -.3 and .3 are best.

         * @param value -0.3 to 0.3 are best, default 0
         */
        set(@FloatRange(from = -0.3, to = 0.3) value) {
            _slope = value
            setFloat(program.slopeLocation, value)
        }

    @CallSuper
    override fun onInit() {
        distance = distance
        slope = slope
    }


    companion object {
        const val DISTANCE = "distance"
        const val SLOPE = "slope"

        const val F_SHADER = """
varying vec2 ${GLSLProgram.TEXTURE_COORDINATE};

uniform sampler2D ${GLSLProgram.INPUT_TEXTURE};

uniform lowp float $DISTANCE;
uniform highp float $SLOPE;

void main() {
   //todo reconsider precision modifiers
   highp vec4 color = vec4(1.0);//todo reimplement as a parameter

   highp float  d = textureCoordinate.y * $SLOPE  +  $DISTANCE;

   highp vec4 c = texture2D(inputImageTexture, textureCoordinate) ; // consider using unpremultiply

   c = (c - d * color) / (1.0 -d);

   gl_FragColor = c; //consider using premultiply(c);
}"""
    }

}