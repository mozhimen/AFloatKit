package com.mozhimen.floatk.window.widgets

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.mozhimen.kotlin.elemk.commons.IAB_Listener
import com.mozhimen.kotlin.utilk.android.view.UtilKViewGroupWrapper
import com.mozhimen.xmlk.basic.widgets.LayoutKFrameTouch

/**
 * @ClassName LayoutKFrameTouchWindow
 * @Description TODO
 * @Author mozhimen
 * @Date 2024/10/12
 * @Version 1.0
 */
class LayoutKFrameTouchWindow : LayoutKFrameTouch {
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

    fun setOnPositionChangedListener(listener: OnPositionChangedListener) {
        _onPositionChangedListener = listener
    }

    //////////////////////////////////////////////////////////////////////

    override fun onMoveXY(desX: Float, desY: Float) {
        _onPositionChangedListener?.onPositionChanged(desX, desY)
    }
}