package com.mozhimen.floatk.window.commons

import android.graphics.RectF
import android.view.View
import android.view.WindowManager
import com.mozhimen.kotlin.utilk.commons.IUtilK

/**
 * @ClassName IFloatKWindowDraggable
 * @Description TODO
 * @Author mozhimen
 * @Date 2024/10/12
 * @Version 1.0
 */
interface IFloatKWindowDragger:IUtilK {
    fun setDragEnable(enable: Boolean)
    fun setAutoMoveToEdge(autoMoveToEdge: Boolean)
    fun setInitMargin(margin: RectF)
    fun update(windowManager: WindowManager, view: View, layoutParams: WindowManager.LayoutParams)
}