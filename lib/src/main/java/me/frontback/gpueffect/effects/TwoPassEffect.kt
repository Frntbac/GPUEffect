package me.frontback.gpueffect.effects

import me.frontback.gpueffect.common.GLSLProgram
import me.frontback.gpueffect.common.GPUEffect
import me.frontback.gpueffect.common.GPUMultiEffect

/**
 * Copyright (C) 2017 Social Apps BVBA
 * Copyright (C) 2012 CyberAgent
 *
 * Adapted from https://github.com/CyberAgent/android-gpuimage/blob/master/library/src/jp/co/cyberagent/android/gpuimage/GPUImageTwoPassFilter.java
 */

open class TwoPassEffect<F : GLSLProgram, S : GLSLProgram> (
        firstProgram : F,
        secondProgram : S
) : GPUMultiEffect(GPUEffect<F>(firstProgram), GPUEffect<S>(secondProgram)) {

    @Suppress("UNCHECKED_CAST")
    open val first: GPUEffect<F>
        get() = effects[0] as GPUEffect<F>

    @Suppress("UNCHECKED_CAST")
    open val second: GPUEffect<S>
        get() = effects[1] as GPUEffect<S>
}