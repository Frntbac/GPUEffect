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

package me.frontback.gpueffect.effects

import me.frontback.gpueffect.common.GLSLProgram
import me.frontback.gpueffect.common.Texture

/**
 * Copyright (C) 2017 Social Apps BVBA
 * Copyright (C) 2012 CyberAgent
 *
 * Adapted from https://github.com/CyberAgent/android-gpuimage/blob/master/library/src/jp/co/cyberagent/android/gpuimage/GPUImageAddBlendFilter.java
 */

open class AddBlendEffect @JvmOverloads constructor(
    texture2 : Texture? = null
) : TwoInputEffect<TwoInputEffect.Program>(texture2, TwoInputEffect.Program(F_SHADER)) {

    companion object {

        const val F_SHADER = """
varying highp vec2 ${GLSLProgram.TEXTURE_COORDINATE};
varying highp vec2 $TEXTURE_COORDINATE2;

uniform sampler2D ${GLSLProgram.INPUT_TEXTURE};
uniform sampler2D $INPUT_TEXTURE2;

void main() {
   lowp vec4 base = texture2D(${GLSLProgram.INPUT_TEXTURE}, ${GLSLProgram.TEXTURE_COORDINATE});
   lowp vec4 overlay = texture2D($INPUT_TEXTURE2, $TEXTURE_COORDINATE2);

   mediump float r;
   if (overlay.r * base.a + base.r * overlay.a >= overlay.a * base.a) {
       r = overlay.a * base.a + overlay.r * (1.0 - base.a) + base.r * (1.0 - overlay.a);
   } else {
       r = overlay.r + base.r;
   }

   mediump float g;
   if (overlay.g * base.a + base.g * overlay.a >= overlay.a * base.a) {
       g = overlay.a * base.a + overlay.g * (1.0 - base.a) + base.g * (1.0 - overlay.a);
   } else {
       g = overlay.g + base.g;
   }

   mediump float b;
   if (overlay.b * base.a + base.b * overlay.a >= overlay.a * base.a) {
       b = overlay.a * base.a + overlay.b * (1.0 - base.a) + base.b * (1.0 - overlay.a);
   } else {
       b = overlay.b + base.b;
   }

   mediump float a = overlay.a + base.a - overlay.a * base.a;

   gl_FragColor = vec4(r, g, b, a);
 }"""
    }
}