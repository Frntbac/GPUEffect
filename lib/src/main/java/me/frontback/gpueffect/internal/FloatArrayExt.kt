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

package me.frontback.gpueffect.internal

/**
 * Copyright (C) 2017 Social Apps BVBA
 * Copyright (C) 2012 CyberAgent
 *
 * Adapted from https://github.com/CyberAgent/android-gpuimage/blob/master/library/src/jp/co/cyberagent/android/gpuimage/util/TextureRotationUtil.java
 */

internal fun FloatArray.flipHorizontally() =
    floatArrayOf(
            this[0].flip(), this[1],
            this[2].flip(), this[3],
            this[4].flip(), this[5],
            this[6].flip(), this[7])

internal fun FloatArray.flipVertically() =
        floatArrayOf(
                this[0], this[1].flip(),
                this[2], this[3].flip(),
                this[4], this[5].flip(),
                this[6], this[7].flip())

private fun Float.flip() = if (this == 0.0f) { 1.0f; } else 0.0f