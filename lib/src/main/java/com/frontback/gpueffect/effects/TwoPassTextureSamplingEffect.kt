package com.frontback.gpueffect.effects

import android.support.annotation.CallSuper
import com.frontback.gpueffect.common.Effect
import com.frontback.gpueffect.common.GLSLProgram

open class TwoPassTextureSamplingEffect<F : TwoPassTextureSamplingEffect.Program, S : TwoPassTextureSamplingEffect.Program>
@JvmOverloads constructor (
        firstProgram: F,
        secondPrgram: S,
        private var _verticalTexelSpacing: Float = 1f,
        private var _horizontalTexelSpacing: Float = 1f
) : TwoPassEffect<F, S>(firstProgram, secondPrgram) {

    open class Program(vertexShader: String, fragmentShader: String) : GLSLProgram(vertexShader, fragmentShader) {
        var texelWidthOffsetLocation: Int = 0
            private set
        var texelHeightOffsetLocation: Int = 0
            private set

        @CallSuper
        override fun onInitialized(programId: Int) {
            super.onInitialized(programId)
            texelWidthOffsetLocation = loadUniformLocation(TEXEL_WIDTH_OFFSET)
            texelHeightOffsetLocation = loadUniformLocation(TEXEL_HEIGHT_OFFSET)
        }
    }

    private var _verticalPassTexelWidthOffset: Float = 0f
    protected open var verticalPassTexelWidthOffset: Float
        get() = _verticalPassTexelWidthOffset
        set(value) {
            _verticalPassTexelWidthOffset = value
            second.setFloat(second.program.texelWidthOffsetLocation, value)
        }

    private var _verticalPassTexelHeightOffset: Float = 0f
    protected open var verticalPassTexelHeightOffset: Float
        get() = _verticalPassTexelHeightOffset
        set(value) {
            _verticalPassTexelHeightOffset = value
            second.setFloat(second.program.texelHeightOffsetLocation, value)
        }

    private var _horizontalPassTexelWidthOffset: Float = 0f
    protected open var horizontalPassTexelWidthOffset: Float
        get() = _horizontalPassTexelWidthOffset
        set(value) {
            _horizontalPassTexelWidthOffset = value
            first.setFloat(first.program.texelWidthOffsetLocation, value)
        }

    private var _horizontalPassTexelHeightOffset: Float = 0f
    protected open var horizontalPassTexelHeightOffset: Float
        get() = _horizontalPassTexelHeightOffset
        set(value) {
            _horizontalPassTexelHeightOffset = value
            first.setFloat(first.program.texelHeightOffsetLocation, value)
        }


    open val horizontalTexelOffsetRatio: Float = 1f

    open val verticalTexelOffsetRatio: Float = 1f

    @CallSuper
    override fun onInit() {
        setOutputSize(outputWidth, outputHeight)
    }

    @CallSuper
    override fun setOutputSize(width: Int, height: Int) = also {
        super.setOutputSize(width, height)
        if (isInitialized) {
            initTexelOffsets()
        }
    }

    protected fun initTexelOffsets() {
        horizontalPassTexelWidthOffset = horizontalTexelOffsetRatio / outputWidth.toFloat()
        horizontalPassTexelHeightOffset = 0.0f

        if (Effect.shouldInvertWidthAndHeight(getRotation())) {
            verticalPassTexelWidthOffset = verticalTexelOffsetRatio / outputHeight.toFloat()
            verticalPassTexelHeightOffset = 0.0f
        } else {
            verticalPassTexelWidthOffset = 0.0f
            verticalPassTexelHeightOffset = verticalTexelOffsetRatio / outputHeight.toFloat()
        }
    }

    companion object {
        const val TEXEL_WIDTH_OFFSET = "texelWidthOffset"
        const val TEXEL_HEIGHT_OFFSET = "texelHeightOffset"
    }
}