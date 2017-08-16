package com.frontback.gpueffect.effects

import com.frontback.gpueffect.common.GLSLProgram
import com.frontback.gpueffect.common.Texture
import java.nio.FloatBuffer

open class BoxBlurEffect(
        private var _blurSize: Float = 1f
) : TwoPassTextureSamplingEffect<BoxBlurEffect.Program, BoxBlurEffect.Program>(
        BoxBlurEffect.Program(), BoxBlurEffect.Program()
) {

    open class Program : TwoPassTextureSamplingEffect.Program(V_SHADER, F_SHADER)

    open var blurSize: Float
        get() = _blurSize
        set(value) {
            _blurSize = value
            initTexelOffsets()
        }

    override fun onInit() {
        super.onInit()
        blurSize = blurSize
    }

    override val verticalTexelOffsetRatio = _blurSize

    override val horizontalTexelOffsetRatio = _blurSize

    companion object {

        const val CENTER = "centerTextureCoordinate"
        const val ONE_STEP_LEFT = "oneStepLeftTextureCoordinate"
        const val TWO_STEP_LEFT = "twoStepsLeftTextureCoordinate"
        const val ONE_STEP_RIGHT = "oneStepRightTextureCoordinate"
        const val TWO_STEP_RIGHT = "twoStepsRightTextureCoordinate"

        const val V_SHADER = """
attribute vec4 ${GLSLProgram.POSITION};
attribute vec2 ${GLSLProgram.INPUT_TEXTURE_COORDINATE};

uniform float $TEXEL_WIDTH_OFFSET;
uniform float $TEXEL_HEIGHT_OFFSET;

varying vec2 $CENTER;
varying vec2 $ONE_STEP_LEFT;
varying vec2 $TWO_STEP_LEFT;
varying vec2 $ONE_STEP_RIGHT;
varying vec2 $TWO_STEP_RIGHT;

void main() {
    gl_Position = ${GLSLProgram.POSITION};

    vec2 firstOffset = vec2(1.5 * $TEXEL_WIDTH_OFFSET, 1.5 * $TEXEL_HEIGHT_OFFSET);
    vec2 secondOffset = vec2(3.5 * $TEXEL_WIDTH_OFFSET, 3.5 * $TEXEL_HEIGHT_OFFSET);

    $CENTER = ${GLSLProgram.INPUT_TEXTURE_COORDINATE};
    $ONE_STEP_LEFT = ${GLSLProgram.INPUT_TEXTURE_COORDINATE} - firstOffset;
    $TWO_STEP_LEFT = ${GLSLProgram.INPUT_TEXTURE_COORDINATE} - secondOffset;
    $ONE_STEP_RIGHT = ${GLSLProgram.INPUT_TEXTURE_COORDINATE} + firstOffset;
    $TWO_STEP_RIGHT = ${GLSLProgram.INPUT_TEXTURE_COORDINATE} + secondOffset;
}"""

        val F_SHADER = """
precision highp float;

uniform sampler2D ${GLSLProgram.INPUT_TEXTURE};

varying vec2 $CENTER;
varying vec2 $ONE_STEP_LEFT;
varying vec2 $TWO_STEP_LEFT;
varying vec2 $ONE_STEP_RIGHT;
varying vec2 $TWO_STEP_RIGHT;

void main() {
    lowp vec4 fragmentColor = texture2D(${GLSLProgram.INPUT_TEXTURE}, $CENTER) * 0.2;
    fragmentColor += texture2D(${GLSLProgram.INPUT_TEXTURE}, $ONE_STEP_LEFT) * 0.2;
    fragmentColor += texture2D(${GLSLProgram.INPUT_TEXTURE}, $ONE_STEP_RIGHT) * 0.2;
    fragmentColor += texture2D(${GLSLProgram.INPUT_TEXTURE}, $TWO_STEP_LEFT) * 0.2;
    fragmentColor += texture2D(${GLSLProgram.INPUT_TEXTURE}, $TWO_STEP_RIGHT) * 0.2;

    gl_FragColor = fragmentColor;
}"""
    }
}