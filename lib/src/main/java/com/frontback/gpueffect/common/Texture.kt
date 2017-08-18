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

package com.frontback.gpueffect.common

interface Texture : Input {

    /**
     * Initialize the texture

     * @return the texture ID
     * *
     * @throws IllegalStateException when the texture couldn't be created correctly
     * @throws IllegalArgumentException when the width or height are < 1
     */
    fun init(width: Int, height: Int): Int

    /**
     * Destroy the texture
     */
    override fun destroy()

    /**
     * Get if the texture was initialized

     * @return whether or not the texture has been initialized
     */
    val isInitialized: Boolean

    /**
     * Get the texture ID
     * @return the texture ID
     * *
     * @throws IllegalStateException when the texture is not initialized
     */
    val id: Int

    /**
     * Get the texture type
     * @return the texture type
     */
    val type: Int

    /**
     * Texture's initialization width
     */
    val width: Int

    /**
     * Texture's initialization height
     */
    val height: Int

    /** @inheritdoc */
    override val texture: Texture
        get() = this
}
