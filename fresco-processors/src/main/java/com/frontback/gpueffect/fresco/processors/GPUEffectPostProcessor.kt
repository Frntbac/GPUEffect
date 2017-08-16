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

package com.frontback.gpueffect.fresco.processors

import android.graphics.Bitmap
import com.facebook.cache.common.CacheKey
import com.facebook.cache.common.SimpleCacheKey
import com.facebook.imagepipeline.request.BasePostprocessor
import com.frontback.gpueffect.common.Effect
import com.frontback.gpueffect.common.GLSLProgram
import com.frontback.gpueffect.common.GPUEffect

open class GPUEffectPostProcessor @JvmOverloads constructor(
        private val effect: Effect = GPUEffect<GLSLProgram>()
) : BasePostprocessor() {

    override fun process(dest: Bitmap?, source: Bitmap?) {
        super.process(dest, source?.let { effect.receives(it).bitmap })
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Effect> getEffect() = effect as T

    override fun getName(): String = effect.javaClass.simpleName

    override fun getPostprocessorCacheKey(): CacheKey = SimpleCacheKey(name)
}