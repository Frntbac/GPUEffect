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

/**
 * This uses Sobel edge detection to place a black border around objects,
 * and then it quantizes the colors present in the image to give a cartoon-like quality to the image.
 */
open class ToonEffect @JvmOverloads constructor(
        private var _threshold: Float = 0.2f,
        private var _quantizationLevels: Float = 10f

) : TextureSampling3x3Effect<ToonEffect.Program>(program = ToonEffect.Program()) {

    open class Program : TextureSampling3x3Effect.Program(fragmentShader = F_SHADER) {

        var thresholdLocation: Int = 0
            private set
        var quantizationLevelsLocation: Int = 0
            private set

        @CallSuper
        public override fun onInitialized(programId: Int) {
            super.onInitialized(programId)
            thresholdLocation = loadUniformLocation(THRESHOLD)
            quantizationLevelsLocation = loadUniformLocation(QUANTIZATION_LEVELS)
        }
    }

    open var threshold: Float
        get() = _threshold
        /**
         * The threshold at which to apply the edges, default of 0.2.

         * @param value default 0.2
         */
        set(value) {
            _threshold = value
            setFloat(program.thresholdLocation, value)
        }
    open var quantizationLevels: Float
        get() = _quantizationLevels
        /**
         * The levels of quantization for the posterization of colors within the scene, with a default of 10.0.

         * @param value default 10.0
         */
        set(value) {
            _quantizationLevels = value
            setFloat(program.quantizationLevelsLocation, value)
        }

    @CallSuper
    override fun onInit() {
        super.onInit()
        threshold = threshold
        quantizationLevels = quantizationLevels
    }


    companion object {
        const val THRESHOLD = "threshold"
        const val QUANTIZATION_LEVELS = "quantizationLevels"

        const val F_SHADER = "" +
                "precision highp float;\n" +
                "\n" +
                "varying vec2 ${GLSLProgram.TEXTURE_COORDINATE};\n" +
                "varying vec2 $LEFT;\n" +
                "varying vec2 $RIGHT;\n" +
                "\n" +
                "varying vec2 $TOP;\n" +
                "varying vec2 $TOP_LEFT;\n" +
                "varying vec2 $TOP_RIGHT;\n" +
                "\n" +
                "varying vec2 $BOTTOM;\n" +
                "varying vec2 $BOTTOM_LEFT;\n" +
                "varying vec2 $BOTTOM_RIGHT;\n" +
                "\n" +
                "uniform sampler2D ${GLSLProgram.INPUT_TEXTURE};\n" +
                "\n" +
                "uniform highp float $THRESHOLD;\n" +
                "uniform highp float $QUANTIZATION_LEVELS;\n" +
                "\n" +
                "const highp vec3 W = vec3(0.2125, 0.7154, 0.0721);\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "   vec4 textureColor = texture2D(${GLSLProgram.INPUT_TEXTURE}, ${GLSLProgram.TEXTURE_COORDINATE});\n" +
                "\n" +
                "   float bottomLeftIntensity = texture2D(${GLSLProgram.INPUT_TEXTURE}, $BOTTOM_LEFT).r;\n" +
                "   float topRightIntensity = texture2D(${GLSLProgram.INPUT_TEXTURE}, $TOP_RIGHT).r;\n" +
                "   float topLeftIntensity = texture2D(${GLSLProgram.INPUT_TEXTURE}, $TOP_LEFT).r;\n" +
                "   float bottomRightIntensity = texture2D(${GLSLProgram.INPUT_TEXTURE}, $BOTTOM_RIGHT).r;\n" +
                "   float leftIntensity = texture2D(${GLSLProgram.INPUT_TEXTURE}, $LEFT).r;\n" +
                "   float rightIntensity = texture2D(${GLSLProgram.INPUT_TEXTURE}, $RIGHT).r;\n" +
                "   float bottomIntensity = texture2D(${GLSLProgram.INPUT_TEXTURE}, $BOTTOM).r;\n" +
                "   float topIntensity = texture2D(${GLSLProgram.INPUT_TEXTURE}, $TOP).r;\n" +
                "   float h = -topLeftIntensity - 2.0 * topIntensity - topRightIntensity + bottomLeftIntensity + 2.0 * bottomIntensity + bottomRightIntensity;\n" +
                "   float v = -bottomLeftIntensity - 2.0 * leftIntensity - topLeftIntensity + bottomRightIntensity + 2.0 * rightIntensity + topRightIntensity;\n" +
                "\n" +
                "   float mag = length(vec2(h, v));\n" +
                "\n" +
                "   vec3 posterizedImageColor = floor((textureColor.rgb * $QUANTIZATION_LEVELS) + 0.5) / $QUANTIZATION_LEVELS;\n" +
                "\n" +
                "   float thresholdTest = 1.0 - step($THRESHOLD, mag);\n" +
                "\n" +
                "   gl_FragColor = vec4(posterizedImageColor * thresholdTest, textureColor.a);\n" +
                "}"
    }
}