/*
 * Copyright (C) 2020 Bandyer S.r.l. All Rights Reserved.
 * See LICENSE.txt for licensing information
 */
package com.bandyer.app_utilities.utils

import android.R
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.graphics.Color
import android.graphics.PointF
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.SystemClock
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.MotionEvent.PointerCoords
import android.view.MotionEvent.PointerProperties
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.bandyer.app_configuration.external_configuration.utils.dp2px
import java.security.Key

/**
 * Helper class to be used to manage navigation in activities with D-Pad hardware.
 * Whenever a key down event is received, the helper function hasConsumedKeyDown(keycode, keyevent)
 * should be called to understand if the key event should be passed to activity's superclass.
 * This functions keeps track of last focused view and highlights it with a pulse animation.
 */
class DPadNavigationHelper {
    private var activity: AppCompatActivity? = null
    private var rootView: ViewGroup? = null
    private var lastFocusedView: View? = null
    private var pulseAnimation: ObjectAnimator? = null

    companion object {
        /**
         * Returns true if a KeyCode event was generated from Dpad.
         * @return Boolean
         */
        fun isDpad(code: Int): Boolean {
            return code == KeyEvent.KEYCODE_SPACE || code == KeyEvent.KEYCODE_TAB || code == KeyEvent.KEYCODE_DPAD_CENTER || code == KeyEvent.KEYCODE_DPAD_LEFT || code == KeyEvent.KEYCODE_DPAD_UP || code == KeyEvent.KEYCODE_DPAD_RIGHT || code == KeyEvent.KEYCODE_ENTER || code == KeyEvent.KEYCODE_DPAD_DOWN
        }
    }

    /**
     * Binds activity to be used in dpad key down evaluation.
     * Remember to call unbind() when activity is destroyed.
     * @param activity
     */
    fun bind(activity: AppCompatActivity?) {
        unbind()
        this.activity = activity
    }

    /**
     * Removes any reference to previous bound activity.
     */
    fun unbind() {
        unHighlightView(lastFocusedView)
        activity = null
        lastFocusedView = null
        rootView = null
    }

    /**
     * Evaluates touch event
     * @param ev event
     * @return true if touch event has been consumed, false otherwise.
     */
    fun hasConsumedTouchEvent(ev: MotionEvent?): Boolean {
        unHighlightView(lastFocusedView)
        return false
    }

    private fun highlightWithDPAD(keyCode: Int, event: KeyEvent): Boolean {
        if (activity == null) return false

        // do not consider non-dpad key codes
        if (!isDpad(keyCode)) return false
        retrieveActivityRootView()

        // enqueue highlight procedure after the os has managed the focus update
        rootView!!.post {
            highlightFocusedView()

            // perfofms a fake click on screen based on in-screen lastFocusedView coordinates
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) clickScreen(getLocationInContainer(lastFocusedView, rootView))
        }

        // let the os manage key codes only if the current focused view has been already highlighted
        return lastFocusedView == null
    }

    /**
     * Evaluates keyUp event
     * @param keyCode event's keycode
     * @param event key event
     * @return true if key event has been consumed, false otherwise.
     */
    fun hasConsumedKeyUp(keyCode: Int, event: KeyEvent) = highlightWithDPAD(keyCode, event)

    /**
     * Evaluates keyDown event
     * @param keyCode event's keycode
     * @param event key event
     * @return true if key event has been consumed, false otherwise.
     */
    fun hasConsumedKeyDown(keyCode: Int, event: KeyEvent) = highlightWithDPAD(keyCode, event)

    /**
     * Retrieves a reference to activity's root view to be used in coordinates calculation for focused views.
     */
    private fun retrieveActivityRootView() {
        if (rootView != null) return
        rootView = (activity!!.findViewById<View>(R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
    }

    /**
     * Highlights focused view with pulse animation
     */
    private fun pulseFocusedView() {
        cancelPulseAnimation(lastFocusedView)
        lastFocusedView = rootView!!.findFocus()
        if (lastFocusedView == null) return
        pulseAnimation = ObjectAnimator.ofPropertyValuesHolder(
                lastFocusedView!!,
                PropertyValuesHolder.ofFloat("scaleX", 1.15f),
                PropertyValuesHolder.ofFloat("scaleY", 1.15f))
        pulseAnimation!!.duration = 750
        pulseAnimation!!.repeatCount = ObjectAnimator.INFINITE
        pulseAnimation!!.repeatMode = ObjectAnimator.REVERSE
        pulseAnimation!!.start()
    }

    /**
     * Highlights current focused view with red stroke foreground
     */
    private fun highlightFocusedView() {
        activity ?: return
        val nextFocusView = activity!!.window.decorView.findFocus()
        if (lastFocusedView == nextFocusView) return
        unHighlightView(lastFocusedView)
        lastFocusedView = nextFocusView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            lastFocusedView?.foreground =
                    GradientDrawable().apply {
                        this.shape = GradientDrawable.RECTANGLE
                        this.setColor(Color.TRANSPARENT)
                        this.setStroke(activity!!.dp2px(3f), Color.RED)
                    }
        } else pulseFocusedView()
    }

    /**
     * Unhighlight view removing its red stroke foreground or removing pulse animation
     * @param view View?
     */
    private fun unHighlightView(view: View?) {
        view ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            view.foreground = null
        else cancelPulseAnimation(view)
    }

    /**
     * Cancels current pulse animation on highlighted view
     * @param v View?
     */
    private fun cancelPulseAnimation(v: View?) {
        if (v == null || pulseAnimation == null) return
        pulseAnimation!!.cancel()
        v.scaleX = 1.0f
        v.scaleY = 1.0f
    }

    /**
     * Performs a click event on screen.
     * @param coordinates input screen coordinated to be used for click event generation.
     */
    private fun clickScreen(coordinates: PointF) {
        val downTime = SystemClock.uptimeMillis()
        val eventTime = SystemClock.uptimeMillis()
        val properties = arrayOfNulls<PointerProperties>(1)
        val pp1 = PointerProperties()
        pp1.id = 0
        pp1.toolType = MotionEvent.TOOL_TYPE_FINGER
        properties[0] = pp1
        val pointerCoords = arrayOfNulls<PointerCoords>(1)
        val pc1 = PointerCoords()
        pc1.x = coordinates.x
        pc1.y = coordinates.y
        pc1.pressure = 1f
        pc1.size = 1f
        pointerCoords[0] = pc1
        var motionEvent = MotionEvent.obtain(downTime, eventTime,
                MotionEvent.ACTION_DOWN, 1, properties,
                pointerCoords, 0, 0, 1f, 1f, 0, 0, 0, 0)
        activity!!.dispatchTouchEvent(motionEvent)
        motionEvent = MotionEvent.obtain(downTime, eventTime,
                MotionEvent.ACTION_UP, 1, properties,
                pointerCoords, 0, 0, 1f, 1f, 0, 0, 0, 0)
        activity!!.dispatchTouchEvent(motionEvent)
    }

    /**
     * Returns target view's location in container as PointF of coordinates.
     * @param target target view.
     * @param container container view used to calculate location.
     * @return Pair<Float></Float>, Float>
     */
    private fun getLocationInContainer(target: View?, container: ViewGroup?): PointF {
        val viewLocation = IntArray(2)
        target!!.getLocationInWindow(viewLocation)
        val rootLocation = IntArray(2)
        container!!.getLocationInWindow(rootLocation)
        return PointF((viewLocation[0] - rootLocation[0]).toFloat(), (viewLocation[1] - rootLocation[1]).toFloat())
    }
}