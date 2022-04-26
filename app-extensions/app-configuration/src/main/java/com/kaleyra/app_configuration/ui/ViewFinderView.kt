/*
 * Copyright 2022 Kaleyra @ https://www.kaleyra.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kaleyra.app_configuration.ui

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.kaleyra.app_configuration.R

class ViewFinderView : View, IViewFinder {
    override var framingRect: Rect? = null
        private set
    private var scannerAlpha = 0
    private val mDefaultLaserColor = resources.getColor(R.color.viewfinder_laser)
    private val mDefaultMaskColor = resources.getColor(R.color.viewfinder_mask)
    private val mDefaultBorderColor = resources.getColor(R.color.viewfinder_border)
    private val mDefaultBorderStrokeWidth = resources.getInteger(R.integer.viewfinder_border_width)
    private val mDefaultBorderLineLength = resources.getInteger(R.integer.viewfinder_border_length)
    protected var mLaserPaint: Paint? = null
    protected var mFinderMaskPaint: Paint? = null
    protected var mBorderPaint: Paint? = null
    protected var mBorderLineLength = 0
    protected var mSquareViewFinder = false
    private var mIsLaserEnabled = true
    private var mBordersAlpha = 0f
    private var mViewFinderOffset = 0

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attributeSet: AttributeSet?) : super(context, attributeSet) {
        init()
    }

    private fun init() {
        //set up laser paint
        mLaserPaint = Paint()
        mLaserPaint!!.color = mDefaultLaserColor
        mLaserPaint!!.style = Paint.Style.FILL

        //finder mask paint
        mFinderMaskPaint = Paint()
        mFinderMaskPaint!!.color = mDefaultMaskColor

        //border paint
        mBorderPaint = Paint()
        mBorderPaint!!.color = mDefaultBorderColor
        mBorderPaint!!.style = Paint.Style.STROKE
        mBorderPaint!!.strokeWidth = mDefaultBorderStrokeWidth.toFloat()
        mBorderPaint!!.isAntiAlias = true
        mBorderLineLength = mDefaultBorderLineLength
    }

    override fun setLaserColor(laserColor: Int) {
        mLaserPaint!!.color = laserColor
    }

    override fun setMaskColor(maskColor: Int) {
        mFinderMaskPaint!!.color = maskColor
    }

    override fun setBorderColor(borderColor: Int) {
        mBorderPaint!!.color = borderColor
    }

    override fun setBorderStrokeWidth(borderStrokeWidth: Int) {
        mBorderPaint!!.strokeWidth = borderStrokeWidth.toFloat()
    }

    override fun setBorderLineLength(borderLineLength: Int) {
        mBorderLineLength = borderLineLength
    }

    override fun setLaserEnabled(isLaserEnabled: Boolean) {
        mIsLaserEnabled = isLaserEnabled
    }

    override fun setBorderCornerRounded(isBorderCornersRounded: Boolean) {
        if (isBorderCornersRounded) {
            mBorderPaint!!.strokeJoin = Paint.Join.ROUND
        } else {
            mBorderPaint!!.strokeJoin = Paint.Join.BEVEL
        }
    }

    override fun setBorderAlpha(alpha: Float) {
        val colorAlpha = (255 * alpha).toInt()
        mBordersAlpha = alpha
        mBorderPaint!!.alpha = colorAlpha
    }

    override fun setBorderCornerRadius(borderCornersRadius: Int) {
        mBorderPaint!!.pathEffect = CornerPathEffect(borderCornersRadius.toFloat())
    }

    override fun setViewFinderOffset(offset: Int) {
        mViewFinderOffset = offset
    }

    // TODO: Need a better way to configure this. Revisit when working on 2.0
    override fun setSquareViewFinder(set: Boolean) {
        mSquareViewFinder = set
    }

    override fun setupViewFinder() {
        updateFramingRect()
        invalidate()
    }

    public override fun onDraw(canvas: Canvas) {
        if (framingRect == null) {
            return
        }
        drawViewFinderMask(canvas)
        drawViewFinderBorder(canvas)
        if (mIsLaserEnabled) {
            drawLaser(canvas)
        }
    }

    fun drawViewFinderMask(canvas: Canvas) {
        val width = canvas.width
        val height = canvas.height
        val framingRect = framingRect
        canvas.drawRect(0f, 0f, width.toFloat(), framingRect!!.top.toFloat(), mFinderMaskPaint!!)
        canvas.drawRect(0f, framingRect.top.toFloat(), framingRect.left.toFloat(), framingRect.bottom + 1.toFloat(), mFinderMaskPaint!!)
        canvas.drawRect(framingRect.right + 1.toFloat(), framingRect.top.toFloat(), width.toFloat(), framingRect.bottom + 1.toFloat(), mFinderMaskPaint!!)
        canvas.drawRect(0f, framingRect.bottom + 1.toFloat(), width.toFloat(), height.toFloat(), mFinderMaskPaint!!)
    }

    fun drawViewFinderBorder(canvas: Canvas) {
        val framingRect = framingRect

        // Top-left corner
        val path = Path()
        path.moveTo(framingRect!!.left.toFloat(), framingRect.top + mBorderLineLength.toFloat())
        path.lineTo(framingRect.left.toFloat(), framingRect.top.toFloat())
        path.lineTo(framingRect.left + mBorderLineLength.toFloat(), framingRect.top.toFloat())
        canvas.drawPath(path, mBorderPaint!!)

        // Top-right corner
        path.moveTo(framingRect.right.toFloat(), framingRect.top + mBorderLineLength.toFloat())
        path.lineTo(framingRect.right.toFloat(), framingRect.top.toFloat())
        path.lineTo(framingRect.right - mBorderLineLength.toFloat(), framingRect.top.toFloat())
        canvas.drawPath(path, mBorderPaint!!)

        // Bottom-right corner
        path.moveTo(framingRect.right.toFloat(), framingRect.bottom - mBorderLineLength.toFloat())
        path.lineTo(framingRect.right.toFloat(), framingRect.bottom.toFloat())
        path.lineTo(framingRect.right - mBorderLineLength.toFloat(), framingRect.bottom.toFloat())
        canvas.drawPath(path, mBorderPaint!!)

        // Bottom-left corner
        path.moveTo(framingRect.left.toFloat(), framingRect.bottom - mBorderLineLength.toFloat())
        path.lineTo(framingRect.left.toFloat(), framingRect.bottom.toFloat())
        path.lineTo(framingRect.left + mBorderLineLength.toFloat(), framingRect.bottom.toFloat())
        canvas.drawPath(path, mBorderPaint!!)
    }

    fun drawLaser(canvas: Canvas) {
        val framingRect = framingRect

        // Draw a red "laser scanner" line through the middle to show decoding is active
        mLaserPaint!!.alpha = SCANNER_ALPHA[scannerAlpha]
        scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.size
        val middle = framingRect!!.height() / 2 + framingRect.top
        canvas.drawRect(framingRect.left + 2.toFloat(), middle - 1.toFloat(), framingRect.right - 1.toFloat(), middle + 2.toFloat(), mLaserPaint!!)
        postInvalidateDelayed(ANIMATION_DELAY,
                framingRect.left - POINT_SIZE,
                framingRect.top - POINT_SIZE,
                framingRect.right + POINT_SIZE,
                framingRect.bottom + POINT_SIZE)
    }

    override fun onSizeChanged(xNew: Int, yNew: Int, xOld: Int, yOld: Int) {
        updateFramingRect()
    }

    @Synchronized
    fun updateFramingRect() {
        val viewResolution = Point(getWidth(), getHeight())
        var width: Int
        var height: Int
        val orientation = context.resources.configuration.orientation
        if (mSquareViewFinder) {
            if (orientation != Configuration.ORIENTATION_PORTRAIT) {
                height = (getHeight() * DEFAULT_SQUARE_DIMENSION_RATIO).toInt()
                width = height
            } else {
                width = (getWidth() * DEFAULT_SQUARE_DIMENSION_RATIO).toInt()
                height = width
            }
        } else {
            if (orientation != Configuration.ORIENTATION_PORTRAIT) {
                height = (getHeight() * LANDSCAPE_HEIGHT_RATIO).toInt()
                width = (LANDSCAPE_WIDTH_HEIGHT_RATIO * height).toInt()
            } else {
                width = (getWidth() * PORTRAIT_WIDTH_RATIO).toInt()
                height = (PORTRAIT_WIDTH_HEIGHT_RATIO * width).toInt()
            }
        }
        if (width > getWidth()) {
            width = getWidth() - MIN_DIMENSION_DIFF
        }
        if (height > getHeight()) {
            height = getHeight() - MIN_DIMENSION_DIFF
        }
        val leftOffset = (viewResolution.x - width) / 2
        val topOffset = (viewResolution.y - height) / 2
        framingRect = Rect(leftOffset + mViewFinderOffset, topOffset + mViewFinderOffset, leftOffset + width - mViewFinderOffset, topOffset + height - mViewFinderOffset)
    }

    companion object {
        private const val TAG = "ViewFinderView"
        private const val PORTRAIT_WIDTH_RATIO = 6f / 8
        private const val PORTRAIT_WIDTH_HEIGHT_RATIO = 0.75f
        private const val LANDSCAPE_HEIGHT_RATIO = 5f / 8
        private const val LANDSCAPE_WIDTH_HEIGHT_RATIO = 1.4f
        private const val MIN_DIMENSION_DIFF = 50
        private const val DEFAULT_SQUARE_DIMENSION_RATIO = 5f / 8
        private val SCANNER_ALPHA = intArrayOf(0, 64, 128, 192, 255, 192, 128, 64)
        private const val POINT_SIZE = 10
        private const val ANIMATION_DELAY = 80L
    }
}
