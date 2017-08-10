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

import com.frontback.gpueffect.common.GLSLProgram
import com.frontback.gpueffect.common.GPUMultiEffect

open class SketchEffect : GPUMultiEffect() {

    init {
        add(GrayscaleEffect())
        add(TextureSampling3x3Effect(program = TextureSampling3x3Effect.Program(F_SHADER)))
    }

    companion object {
        const val F_SHADER = "" +
                "precision mediump float;\n" +
                "\n" +
                "varying vec2 ${GLSLProgram.TEXTURE_COORDINATE};\n" +
                "varying vec2 ${TextureSampling3x3Effect.LEFT};\n" +
                "varying vec2 ${TextureSampling3x3Effect.RIGHT};\n" +
                "\n" +
                "varying vec2 ${TextureSampling3x3Effect.TOP};\n" +
                "varying vec2 ${TextureSampling3x3Effect.TOP_LEFT};\n" +
                "varying vec2 ${TextureSampling3x3Effect.TOP_RIGHT};\n" +
                "\n" +
                "varying vec2 ${TextureSampling3x3Effect.BOTTOM};\n" +
                "varying vec2 ${TextureSampling3x3Effect.BOTTOM_LEFT};\n" +
                "varying vec2 ${TextureSampling3x3Effect.BOTTOM_RIGHT};\n" +
                "\n" +
                "uniform sampler2D ${GLSLProgram.INPUT_TEXTURE};\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "   float bottomLeftIntensity = texture2D(${GLSLProgram.INPUT_TEXTURE}, ${TextureSampling3x3Effect.BOTTOM_LEFT}).r;\n" +
                "   float topRightIntensity = texture2D(${GLSLProgram.INPUT_TEXTURE}, ${TextureSampling3x3Effect.TOP_RIGHT}).r;\n" +
                "   float topLeftIntensity = texture2D(${GLSLProgram.INPUT_TEXTURE}, ${TextureSampling3x3Effect.TOP_LEFT}).r;\n" +
                "   float bottomRightIntensity = texture2D(${GLSLProgram.INPUT_TEXTURE}, ${TextureSampling3x3Effect.BOTTOM_RIGHT}).r;\n" +
                "   float leftIntensity = texture2D(${GLSLProgram.INPUT_TEXTURE}, ${TextureSampling3x3Effect.LEFT}).r;\n" +
                "   float rightIntensity = texture2D(${GLSLProgram.INPUT_TEXTURE}, ${TextureSampling3x3Effect.RIGHT}).r;\n" +
                "   float bottomIntensity = texture2D(${GLSLProgram.INPUT_TEXTURE}, ${TextureSampling3x3Effect.BOTTOM}).r;\n" +
                "   float topIntensity = texture2D(${GLSLProgram.INPUT_TEXTURE}, ${TextureSampling3x3Effect.TOP}).r;\n" +
                "   float h = -topLeftIntensity - 2.0 * topIntensity - topRightIntensity + bottomLeftIntensity + 2.0 * bottomIntensity + bottomRightIntensity;\n" +
                "   float v = -bottomLeftIntensity - 2.0 * leftIntensity - topLeftIntensity + bottomRightIntensity + 2.0 * rightIntensity + topRightIntensity;\n" +
                "\n" +
                "   float mag = 1.0 - length(vec2(h, v));\n" +
                "\n" +
                "   gl_FragColor = vec4(vec3(mag), 1.0);\n" +
                "}\n"
    }
}