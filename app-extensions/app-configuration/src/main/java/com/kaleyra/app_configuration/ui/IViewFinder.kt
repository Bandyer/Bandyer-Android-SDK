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
import android.graphics.Rect

interface IViewFinder {
    fun setLaserColor(laserColor: Int)
    fun setMaskColor(maskColor: Int)
    fun setBorderColor(borderColor: Int)
    fun setBorderStrokeWidth(borderStrokeWidth: Int)
    fun setBorderLineLength(borderLineLength: Int)
    fun setLaserEnabled(isLaserEnabled: Boolean)
    fun setBorderCornerRounded(isBorderCornersRounded: Boolean)
    fun setBorderAlpha(alpha: Float)
    fun setBorderCornerRadius(borderCornersRadius: Int)
    fun setViewFinderOffset(offset: Int)
    fun setSquareViewFinder(isSquareViewFinder: Boolean)

    /**
     * Method that executes when Camera preview is starting.
     * It is recommended to update framing rect here and invalidate view after that. <br></br>
     * For example see: [ViewFinderView.setupViewFinder]
     */
    fun setupViewFinder()

    /**
     * Provides [Rect] that identifies area where barcode scanner can detect visual codes
     *
     * Note: This rect is a area representation in absolute pixel values. <br></br>
     * For example: <br></br>
     * If View's size is 1024x800 so framing rect might be 500x400
     *
     * @return [Rect] that identifies barcode scanner area
     */
    val framingRect: Rect?

//    /**
//     * Width of a [android.view.View] that implements this interface
//     *
//     * Note: this is already implemented in [android.view.View],
//     * so you don't need to override method and provide your implementation
//     *
//     * @return width of a view
//     */
//    val width: Int
//
//    /**
//     * Height of a [android.view.View] that implements this interface
//     *
//     * Note: this is already implemented in [android.view.View],
//     * so you don't need to override method and provide your implementation
//     *
//     * @return height of a view
//     */
//    val height: Int
}