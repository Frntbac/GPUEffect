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

package com.frontback.gpueffect.sample.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import com.frontback.gpueffect.effects.EmbossEffect
import com.frontback.gpueffect.effects.HazeEffect
import com.frontback.gpueffect.sample.R

class ImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        val lena = BitmapFactory.decodeResource(resources, R.drawable.lena)

        val transformed = (HazeEffect() + EmbossEffect())
                .setInput(lena)
                .bitmap

        findViewById<ImageView>(R.id.image).setImageBitmap(transformed)
    }
}
