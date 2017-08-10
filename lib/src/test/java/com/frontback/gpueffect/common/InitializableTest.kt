package com.frontback.gpueffect.common

import org.junit.Test
import org.assertj.core.api.Assertions.*

class InitializableTest {

    val initializable = object : Initializable("object") {}

    @Test(expected = IllegalStateException::class)
    fun testGetIdBeforeSet() {
        assertThat(initializable.isInitialized).isFalse()
        initializable.id
    }

    @Test
    fun testGetIdAfterSet() {
        initializable.id = 2
        assertThat(initializable.isInitialized).isTrue()
        assertThat(initializable.id).isEqualTo(2)
    }

    @Test(expected = IllegalStateException::class)
    fun testGetIdAfterResetting() {
        assertThat(initializable.isInitialized).isFalse()
        initializable.id = 2
        assertThat(initializable.isInitialized).isTrue()
        assertThat(initializable.id).isEqualTo(2)
        initializable.unInitialize()
        assertThat(initializable.isInitialized).isFalse()
        initializable.id
    }
}