package com.mozhimen.floatk.window.widgets

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.mozhimen.kotlin.utilk.android.util.UtilKLogWrapper
import com.mozhimen.kotlin.utilk.android.view.UtilKViewGroupWrapper
import com.mozhimen.xmlk.basic.widgets.LayoutKFrameTouch
import com.mozhimen.xmlk.layoutk.magnet.LayoutKMagnet
import kotlin.math.min

/**
 * @ClassName LayoutKFrameTouchWindow
 * @Description TODO
 * @Author mozhimen
 * @Date 2024/10/12
 * @Version 1.0
 */
class LayoutKFrameTouchWindow : LayoutKMagnet {
    interface OnPositionChangedListener {
        fun onPositionChanged(x: Float, y: Float)
    }

    constructor(context: Context, @LayoutRes resource: Int) : super(context) {
        inflate(context, resource, this)
    }

    constructor(context: Context, view: View) : this(context, view, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))

    constructor(context: Context, view: View, layoutParams: ViewGroup.LayoutParams) : super(context) {
        UtilKViewGroupWrapper.addViewSafe(this, view, layoutParams)
    }

    //////////////////////////////////////////////////////////////////////

    private var _onPositionChangedListener: OnPositionChangedListener? = null

    //////////////////////////////////////////////////////////////////////

    fun setOnPositionChangedListener(listener: OnPositionChangedListener) {
        _onPositionChangedListener = listener
    }

    //////////////////////////////////////////////////////////////////////

    override fun getDesX(event: MotionEvent): Float {
        return /*_lastX +*/ event.rawX - _eventX
    }

    override fun getDesY(event: MotionEvent): Float {
        return /*_lastY +*/ event.rawY - _eventY
    }

    override fun onTouchMoveXY(desX: Float, desY: Float) {
        _lastX = desX
        _lastY = desY
        if (_dragEnable && _onPositionChangedListener != null) {
            _onPositionChangedListener?.onPositionChanged(desX, desY)
        }
    }
}