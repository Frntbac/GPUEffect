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


open class PixelationEffect @JvmOverloads constructor(
        private var _pixel: Float = 1f
) : GPUEffect<PixelationEffect.Program>(PixelationEffect.Program()) {

    open class Program : GLSLProgram(fragmentShader = F_SHADER) {
        var imageWidthFactorLocation: Int = 0
            private set
        var imageHeightFactorLocation: Int = 0
            private set
        var pixelLocation: Int = 0
            private set

        @CallSuper
        override fun onInitialized(programId: Int) {
            imageWidthFactorLocation = loadUniformLocation(IMAGE_WIDTH_FACTOR)
            imageHeightFactorLocation = loadUniformLocation(IMAGE_HEIGHT_FACTOR)
            pixelLocation = loadUniformLocation(PIXEL)
        }
    }

    open var pixel : Float
        get() = _pixel
        set(value) {
            _pixel = value
            setFloat(program.pixelLocation, value)
        }

    @CallSuper
    override fun onInit() {
        pixel = pixel
        setOutputSize(outputWidth, outputHeight)
    }

    @CallSuper
    override fun setOutputSize(width: Int, height: Int) = also {
        super.setOutputSize(width, height)
        if (program.isInitialized) {
            setFloat(program.imageWidthFactorLocation, 1.0f / width)
            setFloat(program.imageHeightFactorLocation, 1.0f / height)
        }
    }


    companion object {

        const val IMAGE_WIDTH_FACTOR = "imageWidthFactor"
        const val IMAGE_HEIGHT_FACTOR = "imageHeightFactor"
        const val PIXEL = "pixel"

        const val F_SHADER = "" +
                "precision highp float;\n" +

                "varying vec2 ${GLSLProgram.TEXTURE_COORDINATE};\n" +

                "uniform float $IMAGE_WIDTH_FACTOR;\n" +
                "uniform float $IMAGE_HEIGHT_FACTOR;\n" +
                "uniform sampler2D ${GLSLProgram.INPUT_TEXTURE};\n" +
                "uniform float $PIXEL;\n" +

                "void main()\n" +
                "{\n" +
                "   vec2 uv  = ${GLSLProgram.TEXTURE_COORDINATE}.xy;\n" +
                "   float dx = $PIXEL * $IMAGE_WIDTH_FACTOR;\n" +
                "   float dy = $PIXEL * $IMAGE_HEIGHT_FACTOR;\n" +
                "   vec2 coord = vec2(dx * floor(uv.x / dx), dy * floor(uv.y / dy));\n" +
                "   vec3 tc = texture2D(${GLSLProgram.INPUT_TEXTURE}, coord).xyz;\n" +
                "   gl_FragColor = vec4(tc, 1.0);\n" +
                "}"
    }
}