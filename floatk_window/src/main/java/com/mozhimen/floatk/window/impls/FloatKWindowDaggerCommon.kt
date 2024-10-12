package com.mozhimen.floatk.window.impls

import android.graphics.RectF
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.mozhimen.floatk.window.commons.IFloatKWindowDragger

/**
 * @ClassName CommonDagger
 * @Description TODO
 * @Author mozhimen
 * @Date 2024/10/12
 * @Version 1.0
 */
class FloatKWindowDaggerCommon : IFloatKWindowDragger {
    private var _dragEnable = false
    private var _autoMoveToEdge = false
    private var _initMargin = RectF()

    override fun setDragEnable(enable: Boolean) {
        _dragEnable = enable
    }

    override fun setAutoMoveToEdge(autoMoveToEdge: Boolean) {
        _autoMoveToEdge = autoMoveToEdge
    }

    override fun setInitMargin(margin: RectF) {
        _initMargin = margin
    }

    override fun update(windowManager: WindowManager, view: View, layoutParams: WindowManager.LayoutParams) {
        try {
            Log.d(TAG, "update: layoutParams $layoutParams")
            windowManager.updateViewLayout(view, layoutParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}