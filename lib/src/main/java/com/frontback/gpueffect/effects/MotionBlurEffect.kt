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

open class MotionBlurEffect @JvmOverloads constructor(
        private var _angle: Float = 0f,
        private var _size: Float = 2.5f
) : GPUEffect<MotionBlurEffect.Program>(MotionBlurEffect.Program()) {

    open class Program : GLSLProgram(V_SHADER, FRAGMENT_SHADER) {
        var uDirectionTexelStep: Int = 0
            private set
        @CallSuper
        override fun onInitialized(programId: Int) {
            uDirectionTexelStep = loadUniformLocation(DIRECTIONAL_TEXEL_STEP)
        }
    }

    protected val texelOffsets: FloatArray = FloatArray(2)

    open var angle: Float
        get() = _angle
        set(value) {
            _angle = value
            recalculateTexelOffsets()
        }

    open var size: Float
        get() = _size
        set(value) {
            _size = value
            recalculateTexelOffsets()
        }

    @CallSuper
    override fun onInit() {
        super.onInit()
        setOutputSize(outputWidth, outputHeight)
    }


    @CallSuper
    override fun setOutputSize(width: Int, height: Int) = also {
        super.setOutputSize(width, height)
        if (isInitialized) {
            recalculateTexelOffsets()
        }
    }

    open protected fun recalculateTexelOffsets() {
        if (outputHeight == 0) {
            return
        }
        val aspectRatio = outputWidth / outputHeight
        texelOffsets[0] = (size.toDouble() * Math.sin(angle * Math.PI / 180f) * aspectRatio.toDouble() / outputHeight).toFloat()
        texelOffsets[1] = (size * Math.cos(angle * Math.PI / 180f) / outputHeight).toFloat()

        setFloatVec2(program.uDirectionTexelStep, texelOffsets)
    }


    companion object {
        const val DIRECTIONAL_TEXEL_STEP = "directionalTexelStep"

        const val V_SHADER = """
attribute vec4 ${GLSLProgram.POSITION};
attribute vec4 ${GLSLProgram.INPUT_TEXTURE};
uniform vec2 $DIRECTIONAL_TEXEL_STEP;
varying vec2 ${GLSLProgram.TEXTURE_COORDINATE};
varying vec2 oneStepBackTextureCoordinate;
varying vec2 twoStepsBackTextureCoordinate;
varying vec2 threeStepsBackTextureCoordinate;
varying vec2 fourStepsBackTextureCoordinate;
varying vec2 oneStepForwardTextureCoordinate;
varying vec2 twoStepsForwardTextureCoordinate;
varying vec2 threeStepsForwardTextureCoordinate;
varying vec2 fourStepsForwardTextureCoordinate;
void main() {
   gl_Position = ${GLSLProgram.POSITION};
   ${GLSLProgram.TEXTURE_COORDINATE} = ${GLSLProgram.INPUT_TEXTURE}.xy;
   oneStepBackTextureCoordinate = ${GLSLProgram.INPUT_TEXTURE}.xy - $DIRECTIONAL_TEXEL_STEP;
   twoStepsBackTextureCoordinate = ${GLSLProgram.INPUT_TEXTURE}.xy - 2.0 * $DIRECTIONAL_TEXEL_STEP;
   threeStepsBackTextureCoordinate = ${GLSLProgram.INPUT_TEXTURE}.xy - 3.0 * $DIRECTIONAL_TEXEL_STEP;
   fourStepsBackTextureCoordinate = ${GLSLProgram.INPUT_TEXTURE}.xy - 4.0 * $DIRECTIONAL_TEXEL_STEP;
   oneStepForwardTextureCoordinate = ${GLSLProgram.INPUT_TEXTURE}.xy + $DIRECTIONAL_TEXEL_STEP;
   twoStepsForwardTextureCoordinate = ${GLSLProgram.INPUT_TEXTURE}.xy + 2.0 * $DIRECTIONAL_TEXEL_STEP;
   threeStepsForwardTextureCoordinate = ${GLSLProgram.INPUT_TEXTURE}.xy + 3.0 * $DIRECTIONAL_TEXEL_STEP;
   fourStepsForwardTextureCoordinate = ${GLSLProgram.INPUT_TEXTURE}.xy + 4.0 * $DIRECTIONAL_TEXEL_STEP;
}"""

        const val FRAGMENT_SHADER = """
precision highp float;
uniform sampler2D ${GLSLProgram.INPUT_TEXTURE};
varying vec2 ${GLSLProgram.TEXTURE_COORDINATE};
varying vec2 oneStepBackTextureCoordinate;
varying vec2 twoStepsBackTextureCoordinate;
varying vec2 threeStepsBackTextureCoordinate;
varying vec2 fourStepsBackTextureCoordinate;
varying vec2 oneStepForwardTextureCoordinate;
varying vec2 twoStepsForwardTextureCoordinate;
varying vec2 threeStepsForwardTextureCoordinate;
varying vec2 fourStepsForwardTextureCoordinate;
void main() {
   lowp vec4 fragmentColor = texture2D(${GLSLProgram.INPUT_TEXTURE}, ${GLSLProgram.TEXTURE_COORDINATE}) * 0.18;
   fragmentColor += texture2D(${GLSLProgram.INPUT_TEXTURE}, oneStepBackTextureCoordinate) * 0.15;
   fragmentColor += texture2D(${GLSLProgram.INPUT_TEXTURE}, twoStepsBackTextureCoordinate) *  0.12;
   fragmentColor += texture2D(${GLSLProgram.INPUT_TEXTURE}, threeStepsBackTextureCoordinate) * 0.09;
   fragmentColor += texture2D(${GLSLProgram.INPUT_TEXTURE}, fourStepsBackTextureCoordinate) * 0.05;
   fragmentColor += texture2D(${GLSLProgram.INPUT_TEXTURE}, oneStepForwardTextureCoordinate) * 0.15;
   fragmentColor += texture2D(${GLSLProgram.INPUT_TEXTURE}, twoStepsForwardTextureCoordinate) *  0.12;
   fragmentColor += texture2D(${GLSLProgram.INPUT_TEXTURE}, threeStepsForwardTextureCoordinate) * 0.09;
   fragmentColor += texture2D(${GLSLProgram.INPUT_TEXTURE}, fourStepsForwardTextureCoordinate) * 0.05;
   gl_FragColor = fragmentColor;
}"""
    }

}