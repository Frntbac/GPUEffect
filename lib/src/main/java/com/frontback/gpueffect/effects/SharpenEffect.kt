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
 * Sharpens the picture.
 *
 * sharpness: from -4.0 to 4.0, with 0.0 as the normal level
 */
open class SharpenEffect @JvmOverloads constructor(
        @FloatRange(from = -4.0, to = 4.0) private var _sharpness: Float = 0.0f
) : GPUEffect<SharpenEffect.Program>(SharpenEffect.Program()) {

    open class Program : GLSLProgram(V_SHADER, F_SHADER) {
        var sharpnessLocation: Int = 0
            private set
        var imageWidthFactorLocation: Int = 0
            private set
        var imageHeightFactorLocation: Int = 0
            private set

        @CallSuper
        override fun onInitialized(programId: Int) {
            sharpnessLocation = loadUniformLocation(SHARPNESS)
            imageWidthFactorLocation = loadUniformLocation(IMAGE_WIDTH_FACTOR)
            imageHeightFactorLocation = loadUniformLocation(IMAGE_HEIGHT_FACTOR)
        }
    }

    open var sharpness: Float
        get() = _sharpness
        /**
         * Set effect's sharpness
         *
         * @param value from -4.0 to 4.0, with 0.0 as the normal level
         */
        set(@FloatRange(from = -4.0, to = 4.0) value) {
            _sharpness = value
            setFloat(program.sharpnessLocation, value)
        }

    @CallSuper
    override fun onInit() {
        sharpness = sharpness
    }

    override fun setOutputSize(width: Int, height: Int) = also {
        super.setOutputSize(width, height)
        setFloat(program.imageWidthFactorLocation, 1.0f / width)
        setFloat(program.imageHeightFactorLocation, 1.0f / height)
    }

    companion object {

        const val SHARPNESS = "sharpness"
        const val IMAGE_WIDTH_FACTOR = "imageWidthFactor"
        const val IMAGE_HEIGHT_FACTOR = "imageHeightFactor"

        private const val LEFT = "leftTextureCoordinate"
        private const val RIGHT = "rightTextureCoordinate"
        private const val TOP = "topTextureCoordinate"
        private const val BOTTOM = "bottomTextureCoordinate"
        private const val CENTER_MULTIPLIER = "centerMultiplier"
        private const val EDGE_MULTIPLIER = "edgeMultiplier"

        const val V_SHADER = "" +
                "attribute vec4 position;\n" +
                "attribute vec4 ${GLSLProgram.INPUT_TEXTURE_COORDINATE};\n" +
                "\n" +
                "uniform float $IMAGE_WIDTH_FACTOR; \n" +
                "uniform float $IMAGE_HEIGHT_FACTOR; \n" +
                "uniform float $SHARPNESS;\n" +
                "\n" +
                "varying vec2 ${GLSLProgram.TEXTURE_COORDINATE};\n" +
                "varying vec2 $LEFT;\n" +
                "varying vec2 $RIGHT; \n" +
                "varying vec2 $TOP;\n" +
                "varying vec2 $BOTTOM;\n" +
                "\n" +
                "varying float $CENTER_MULTIPLIER;\n" +
                "varying float $EDGE_MULTIPLIER;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "   gl_Position = position;\n" +
                "\n" +
                "   mediump vec2 widthStep = vec2($IMAGE_WIDTH_FACTOR, 0.0);\n" +
                "   mediump vec2 heightStep = vec2(0.0, $IMAGE_HEIGHT_FACTOR);\n" +
                "\n" +
                "   ${GLSLProgram.TEXTURE_COORDINATE} = ${GLSLProgram.INPUT_TEXTURE_COORDINATE}.xy;\n" +
                "   $LEFT = ${GLSLProgram.INPUT_TEXTURE_COORDINATE}.xy - widthStep;\n" +
                "   $RIGHT = ${GLSLProgram.INPUT_TEXTURE_COORDINATE}.xy + widthStep;\n" +
                "   $TOP = ${GLSLProgram.INPUT_TEXTURE_COORDINATE}.xy + heightStep;     \n" +
                "   $BOTTOM = ${GLSLProgram.INPUT_TEXTURE_COORDINATE}.xy - heightStep;\n" +
                "\n" +
                "   $CENTER_MULTIPLIER = 1.0 + 4.0 * $SHARPNESS;\n" +
                "   $EDGE_MULTIPLIER = $SHARPNESS;\n" +
                "}"

        const val F_SHADER = "" +
                "precision highp float;\n" +
                "\n" +
                "varying highp vec2 ${GLSLProgram.TEXTURE_COORDINATE};\n" +
                "varying highp vec2 $LEFT;\n" +
                "varying highp vec2 $RIGHT; \n" +
                "varying highp vec2 $TOP;\n" +
                "varying highp vec2 $BOTTOM;\n" +
                "\n" +
                "varying highp float $CENTER_MULTIPLIER;\n" +
                "varying highp float $EDGE_MULTIPLIER;\n" +
                "\n" +
                "uniform sampler2D ${GLSLProgram.INPUT_TEXTURE};\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "   mediump vec3 textureColor = texture2D(${GLSLProgram.INPUT_TEXTURE}, ${GLSLProgram.TEXTURE_COORDINATE}).rgb;\n" +
                "   mediump vec3 leftTextureColor = texture2D(${GLSLProgram.INPUT_TEXTURE}, $LEFT).rgb;\n" +
                "   mediump vec3 rightTextureColor = texture2D(${GLSLProgram.INPUT_TEXTURE}, $RIGHT).rgb;\n" +
                "   mediump vec3 topTextureColor = texture2D(${GLSLProgram.INPUT_TEXTURE}, $TOP).rgb;\n" +
                "   mediump vec3 bottomTextureColor = texture2D(${GLSLProgram.INPUT_TEXTURE}, $BOTTOM).rgb;\n" +
                "\n" +
                "   gl_FragColor = vec4((textureColor * $CENTER_MULTIPLIER - (leftTextureColor * $EDGE_MULTIPLIER + rightTextureColor * $EDGE_MULTIPLIER + topTextureColor * $EDGE_MULTIPLIER + bottomTextureColor * $EDGE_MULTIPLIER)), texture2D(${GLSLProgram.INPUT_TEXTURE}, bottomTextureCoordinate).w);\n" +
                "}"
    }
}