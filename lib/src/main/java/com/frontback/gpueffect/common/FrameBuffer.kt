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

/**
 * Interface to abstract the
 */
interface FrameBuffer {

    /**
     * Initialize the frame buffer

     * @param width width of the frame buffer
     * *
     * @param height height of the frame buffer
     * *
     * @return the FrameBuffer ID
     * *
     * @throws IllegalStateException when the frame buffer couldn't be initialized correctly
     * @throws IllegalArgumentException when the width or height are < 1
     */
    fun init(width: Int, height: Int): Int

    /**
     * Get if the frame buffer was initialized

     * @return whether or not the frame buffer has been initialized
     */
    val isInitialized: Boolean

    /**
     * Destroy the FrameBuffer
     */
    fun destroy()

    /**
     * Get the frame buffer ID
     * @return the frame buffer ID
     * *
     * @throws IllegalStateException when the frame buffer is not initialized
     */
    val id: Int

    /**
     * Get the attached texture

     * @return the Texture object in which the frame buffer draws
     */
    val texture: Texture

    /**
     * Texture's initialization width
     */
    val width: Int

    /**
     * Texture's initialization height
     */
    val height: Int

}
