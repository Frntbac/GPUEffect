package com.frontback.gpueffect.common

import android.annotation.SuppressLint
import com.nhaarman.mockito_kotlin.KArgumentCaptor
import com.nhaarman.mockito_kotlin.doNothing
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import org.junit.Test
import org.assertj.core.api.Assertions.*
import org.mockito.ArgumentCaptor

class GPUEffectTest {

    val program = mock<GLSLProgram>()
    val effect = spy(GPUEffect(program))

    @Test
    fun testInvertRotation() {
        effect.setRotation(Rotation.NONE)
        effect.setOutputSize(100, 200)
        assertThat(effect.outputWidth).isEqualTo(100)
        assertThat(effect.outputHeight).isEqualTo(200)
        effect.setRotation(Rotation._90)
        assertThat(effect.outputWidth).isEqualTo(200)
        assertThat(effect.outputHeight).isEqualTo(100)
        effect.setRotation(Rotation._270)
        assertThat(effect.outputWidth).isEqualTo(200)
        assertThat(effect.outputHeight).isEqualTo(100)
        effect.setRotation(Rotation._180)
        assertThat(effect.outputWidth).isEqualTo(100)
        assertThat(effect.outputHeight).isEqualTo(200)
        effect.setRotation(Rotation.NONE)
        assertThat(effect.outputWidth).isEqualTo(100)
        assertThat(effect.outputHeight).isEqualTo(200)
    }

    @Test
    fun testRotationSetBufferCorrectly() {
        val captor = KArgumentCaptor<FloatArray>(ArgumentCaptor.forClass(FloatArray::class.java), FloatArray::class)
        doNothing().`when`(effect).setRotationArray(captor.capture())
        effect.setRotation(Rotation.NONE)
        effect.setRotation(Rotation._90)
        effect.setRotation(Rotation._180)
        effect.setRotation(Rotation._270)
        effect.setRotation(Rotation.UPSIDE_DOWN)
        effect.setRotation(Rotation.UPSIDE_DOWN_90)
        effect.setRotation(Rotation.UPSIDE_DOWN_180)
        effect.setRotation(Rotation.UPSIDE_DOWN_270)
        assertThat(captor.allValues[0]).isEqualTo(Rotation.ARRAY_NONE)
        assertThat(captor.allValues[1]).isEqualTo(Rotation.ARRAY_90)
        assertThat(captor.allValues[2]).isEqualTo(Rotation.ARRAY_180)
        assertThat(captor.allValues[3]).isEqualTo(Rotation.ARRAY_270)
        assertThat(captor.allValues[4]).isEqualTo(Rotation.ARRAY_UPSIDE_DOWN)
        assertThat(captor.allValues[5]).isEqualTo(Rotation.ARRAY_UPSIDE_DOWN_90)
        assertThat(captor.allValues[6]).isEqualTo(Rotation.ARRAY_UPSIDE_DOWN_180)
        assertThat(captor.allValues[7]).isEqualTo(Rotation.ARRAY_UPSIDE_DOWN_270)
    }

    @SuppressLint("WrongConstant")
    @Test(expected = IllegalArgumentException::class)
    fun testSetWrongRotation() {
        effect.setRotation(2)
    }

    @Test
    fun setOutputSizeWithRotation() {
        effect.setRotation(Rotation.NONE)
        effect.setOutputSize(100, 200)
        assertThat(effect.outputWidth).isEqualTo(100)
        assertThat(effect.outputHeight).isEqualTo(200)
        effect.setRotation(Rotation._90)
        effect.setOutputSize(100, 200)
        assertThat(effect.outputWidth).isEqualTo(200)
        assertThat(effect.outputHeight).isEqualTo(100)
        effect.setRotation(Rotation._180)
        effect.setOutputSize(100, 200)
        assertThat(effect.outputWidth).isEqualTo(100)
        assertThat(effect.outputHeight).isEqualTo(200)
        effect.setRotation(Rotation._270)
        effect.setOutputSize(100, 200)
        assertThat(effect.outputWidth).isEqualTo(200)
        assertThat(effect.outputHeight).isEqualTo(100)

        effect.setRotation(Rotation.UPSIDE_DOWN)
        effect.setOutputSize(100, 200)
        assertThat(effect.outputWidth).isEqualTo(100)
        assertThat(effect.outputHeight).isEqualTo(200)
        effect.setRotation(Rotation.UPSIDE_DOWN_90)
        effect.setOutputSize(100, 200)
        assertThat(effect.outputWidth).isEqualTo(200)
        assertThat(effect.outputHeight).isEqualTo(100)
        effect.setRotation(Rotation.UPSIDE_DOWN_180)
        effect.setOutputSize(100, 200)
        assertThat(effect.outputWidth).isEqualTo(100)
        assertThat(effect.outputHeight).isEqualTo(200)
        effect.setRotation(Rotation.UPSIDE_DOWN_270)
        effect.setOutputSize(100, 200)
        assertThat(effect.outputWidth).isEqualTo(200)
        assertThat(effect.outputHeight).isEqualTo(100)
    }
}