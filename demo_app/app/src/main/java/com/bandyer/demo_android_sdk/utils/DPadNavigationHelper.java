package com.bandyer.demo_android_sdk.utils;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.PointF;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Helper class to be used to manage navigation in activities with D-Pad hardware.
 * Whenever a key down event is received, the helper function hasConsumedKeyDown(keycode, keyevent)
 * should be called to understand if the key event should be passed to activity's superclass.
 * This functions keeps track of last focused view and highlights it with a pulse animation.
 */
public class DPadNavigationHelper {

    private AppCompatActivity activity = null;
    private ViewGroup rootView = null;
    private View lastFocusedView = null;
    private ObjectAnimator pulseAnimation = null;

    /**
     * Binds activity to be used in dpad key down evaluation.
     * Remember to call unbind() when activity is destroyed.
     * @param activity
     */
    public void bind(AppCompatActivity activity) {
        unbind();
        this.activity = activity;
    }

    /**
     * Removes any reference to previous bound activity.
     */
    public void unbind() {
        cancelPulseView(lastFocusedView);
        this.activity = null;
        this.lastFocusedView = null;
        this.rootView = null;
    }

    /**
     * Evaluates keyDown event
     * @param keyCode event's keycode
     * @param event key event
     * @return true if key event has been consumed, false otherwise.
     */
    public boolean hasConsumedKeyDown(final int keyCode, KeyEvent event) {

        if (activity == null) return false;

        // consider only action down events
        if (event.getAction() != KeyEvent.ACTION_DOWN) return false;

        // do not consider non-dpad key codes
        if (!isDpad(keyCode)) return false;

        retrieveActivityRootView();

        // enqueue highlight procedure after the os has managed the focus update
        rootView.post(new Runnable() {
            @Override
            public void run() {
                pulseFocusedView();

                // perfofms a fake click on screen based on in-screen lastFocusedView coordinates
                if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER)
                    clickScreen(getLocationInContainer(lastFocusedView, rootView));
            }
        });

        // let the os manage key codes only if the current focused view has been already highlighted
        return lastFocusedView == null;
    }

    /**
     * Retrieves a reference to activity's root view to be used in coordinates calculation for focused views.
     */
    private void retrieveActivityRootView() {
        if (rootView != null) return;
        this.rootView = (ViewGroup) ((ViewGroup) this.activity.findViewById(android.R.id.content)).getChildAt(0);
    }

    /**
     * Returns true if a KeyCode event was generated from Dpad.
     * @return Boolean
     */
    private boolean isDpad(int code) {
        return code == KeyEvent.KEYCODE_DPAD_CENTER ||
                code == KeyEvent.KEYCODE_DPAD_LEFT ||
                code == KeyEvent.KEYCODE_DPAD_UP ||
                code == KeyEvent.KEYCODE_DPAD_RIGHT ||
                code == KeyEvent.KEYCODE_ENTER ||
                code == KeyEvent.KEYCODE_DPAD_DOWN;
    }

    /**
     * Highlights any focused view after a dpad key event has been received.
     */
    private void pulseFocusedView() {
        cancelPulseView(lastFocusedView);
        lastFocusedView = rootView.findFocus();
        if (lastFocusedView == null) return;
        pulseAnimation = ObjectAnimator.ofPropertyValuesHolder(
                lastFocusedView,
                PropertyValuesHolder.ofFloat("scaleX", 1.15f),
                PropertyValuesHolder.ofFloat("scaleY", 1.15f));
        pulseAnimation.setDuration(750);
        pulseAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        pulseAnimation.setRepeatMode(ObjectAnimator.REVERSE);
        pulseAnimation.start();
    }

    /**
     * Cancel pulse animation.
     * @param v the target view that should stop animating.
     */
    private void cancelPulseView(View v) {
        if (v == null || pulseAnimation == null) return;
        pulseAnimation.cancel();
        v.setScaleX(1.0f);
        v.setScaleY(1.0f);
    }

    /**
     * Performs a click event on screen.
     * @param coordinates input screen coordinated to be used for click event generation.
     */
    private void clickScreen(PointF coordinates) {
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        MotionEvent.PointerProperties[] properties = new MotionEvent.PointerProperties[1];
        MotionEvent.PointerProperties pp1 = new MotionEvent.PointerProperties();
        pp1.id = 0;
        pp1.toolType = MotionEvent.TOOL_TYPE_FINGER;
        properties[0] = pp1;
        MotionEvent.PointerCoords[] pointerCoords = new MotionEvent.PointerCoords[1];
        MotionEvent.PointerCoords pc1 = new MotionEvent.PointerCoords();
        pc1.x = coordinates.x;
        pc1.y = coordinates.y;
        pc1.pressure = 1f;
        pc1.size = 1f;
        pointerCoords[0] = pc1;
        MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime,
                MotionEvent.ACTION_DOWN, 1, properties,
                pointerCoords, 0, 0, 1f, 1f, 0, 0, 0, 0);
        activity.dispatchTouchEvent(motionEvent);

        motionEvent = MotionEvent.obtain(downTime, eventTime,
                MotionEvent.ACTION_UP, 1, properties,
                pointerCoords, 0, 0, 1f, 1f, 0, 0, 0, 0);
        activity.dispatchTouchEvent(motionEvent);
    }

    /**
     * Returns target view's location in container as PointF of coordinates.
     * @param target target view.
     * @param container container view used to calculate location.
     * @return Pair<Float, Float>
     */
    private PointF getLocationInContainer(View target, ViewGroup container) {
        int[] viewLocation = new int[2];
        target.getLocationInWindow(viewLocation);
        int[] rootLocation = new int[2];
        container.getLocationInWindow(rootLocation);
        return new PointF(viewLocation[0] - rootLocation[0], viewLocation[1] - rootLocation[1]);
    }
}
