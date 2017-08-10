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

import android.support.annotation.VisibleForTesting

abstract class Initializable(val name: String) {

    private var _id = NO_ID
    var id: Int
        /**
         * Get the object ID
         * @return the object ID
         *
         * @throws IllegalStateException when the object is not initialized
         */
        get() {
            if (_id == NO_ID) {
                throw IllegalStateException("$name has not be initialized")
            }
            return _id
        }
        @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
        protected set(value) {
            _id = value
        }

    /**
     * Returns whether the object was initialized

     * @return whether or not the object has been initialized
     */
    val isInitialized: Boolean
        get() = _id != NO_ID

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    open fun unInitialize() {
        _id = NO_ID
    }

    companion object {
        private const val NO_ID = -1
    }
}