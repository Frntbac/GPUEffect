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

package me.frontback.gpueffect.sample.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import me.frontback.gpueffect.sample.R


class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.btImage).setOnClickListener(this)
        findViewById<View>(R.id.btFresco).setOnClickListener(this)
        findViewById<View>(R.id.btSurface).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btImage -> startActivity(Intent(this, ImageActivity::class.java))
            R.id.btFresco -> startActivity(Intent(this, FrescoImageActivity::class.java))
            R.id.btSurface -> startActivity(Intent(this, SurfaceActivity::class.java))
        }
    }
}
