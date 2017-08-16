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

import android.graphics.PointF
import android.support.annotation.CallSuper
import com.frontback.gpueffect.common.GLSLProgram
import com.frontback.gpueffect.common.GPUEffect

open class VignetteEffect @JvmOverloads constructor(
        private var _center: PointF = PointF(0.5f, 0.5f),
        private var _color: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f),
        private var _start: Float = 0.3f,
        private var _end: Float = 0.75f
) : GPUEffect<VignetteEffect.Program>(VignetteEffect.Program()) {

    open class Program : GLSLProgram(fragmentShader = F_SHADER) {

        var centerLocation: Int = 0
            private set
        var colorLocation: Int = 0
            private set
        var startLocation: Int = 0
            private set
        var endLocation: Int = 0
            private set

        @CallSuper
        override fun onInitialized(programId: Int) {
            centerLocation = loadUniformLocation(CENTER)
            colorLocation = loadUniformLocation(COLOR)
            startLocation = loadUniformLocation(START)
            endLocation = loadUniformLocation(END)
        }
    }

    open var center: PointF
        get() = _center
        set(value) {
            _center = value
            setPoint(program.centerLocation, value)
        }
    open var color: FloatArray
        get() = _color
        set(value) {
            _color = value
            setFloatVec3(program.colorLocation, value)
        }
    open var start: Float
        get() = _start
        set(value) {
            _start = value
            setFloat(program.startLocation, value)
        }
    open var end: Float
        get() = _end
        set(value) {
            _end = value
            setFloat(program.endLocation, value)
        }

    @CallSuper
    override fun onInit() {
        center = center
        color = color
        start = start
        end = end
    }

    companion object {

        const val CENTER = "vignetteCenter"
        const val COLOR = "vignetteColor"
        const val START = "vignetteStart"
        const val END = "vignetteEnd"

        const val F_SHADER = """
uniform sampler2D ${GLSLProgram.INPUT_TEXTURE};
varying highp vec2 ${GLSLProgram.TEXTURE_COORDINATE};

uniform lowp vec2 $CENTER;
uniform lowp vec3 $COLOR;
uniform highp float $START;
uniform highp float $END;

void main() {
    lowp vec3 rgb = texture2D(${GLSLProgram.INPUT_TEXTURE}, ${GLSLProgram.TEXTURE_COORDINATE}).rgb;
    lowp float d = distance(textureCoordinate, $CENTER);
    lowp float percent = smoothstep($START, $END, d);
    gl_FragColor = vec4(mix(rgb.x, $COLOR.x, percent), mix(rgb.y, $COLOR.y, percent), mix(rgb.z, $COLOR.z, percent), 1.0);
}"""
    }
}