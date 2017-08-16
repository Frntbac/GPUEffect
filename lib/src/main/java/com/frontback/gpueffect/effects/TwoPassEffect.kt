package com.frontback.gpueffect.effects

import com.frontback.gpueffect.common.GLSLProgram
import com.frontback.gpueffect.common.GPUEffect
import com.frontback.gpueffect.common.GPUMultiEffect

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