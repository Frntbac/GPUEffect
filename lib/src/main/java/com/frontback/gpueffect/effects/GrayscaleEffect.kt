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
import com.frontback.gpueffect.common.GPUEffect

/**
 * Copyright (C) 2017 Social Apps BVBA
 * Copyright (C) 2012 CyberAgent
 *
 * Adapted from https://github.com/CyberAgent/android-gpuimage/blob/master/library/src/jp/co/cyberagent/android/gpuimage/GPUImageGrayscaleFilter.java
 */

open class GrayscaleEffect
    : GPUEffect<GLSLProgram>(GLSLProgram(fragmentShader = F_SHADER)) {

    companion object {

        const val F_SHADER = """
precision mediump float;
varying vec2 ${GLSLProgram.TEXTURE_COORDINATE};
uniform sampler2D ${GLSLProgram.INPUT_TEXTURE};

const mediump vec3 W = vec3(0.2125, 0.7154, 0.0721);

void main() {
   lowp vec4 textureColor = texture2D(${GLSLProgram.INPUT_TEXTURE}, ${GLSLProgram.TEXTURE_COORDINATE});
   float luminance = dot(textureColor.rgb, W);

   gl_FragColor = vec4(vec3(luminance), textureColor.a);
}"""
    }
}
