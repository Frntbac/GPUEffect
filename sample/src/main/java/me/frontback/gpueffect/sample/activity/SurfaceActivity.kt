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

import android.graphics.BitmapFactory
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import me.frontback.gpueffect.common.Effect
import me.frontback.gpueffect.common.Rotation
import me.frontback.gpueffect.effects.GrayscaleEffect
import me.frontback.gpueffect.sample.R
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SurfaceActivity : AppCompatActivity(), GLSurfaceView.Renderer {

    lateinit private var effect: Effect

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_surface)

        effect = GrayscaleEffect() receives BitmapFactory.decodeResource(resources, R.drawable.lena)
        // We have to do this as the bitmap is interpreted from top left to bottom right but
        // we draw from bottom left to top right
        effect.setRotation(Rotation.UPSIDE_DOWN)

        val surface = findViewById<GLSurfaceView>(R.id.surface)
        surface.setEGLContextClientVersion(2)
        surface.setRenderer(this)
        surface.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        effect.init()
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        effect.setOutputSize(width, height)
    }

    override fun onDrawFrame(gl: GL10) {
        effect.draw()
    }
}
