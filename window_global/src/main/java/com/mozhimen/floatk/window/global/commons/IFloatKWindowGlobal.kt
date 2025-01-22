package com.mozhimen.floatk.window.global.commons

import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import com.mozhimen.floatk.window.commons.IFloatKWindowDragger
import com.mozhimen.kotlin.elemk.commons.IExt_Listener
import java.lang.ref.WeakReference

/**
 * @ClassName IFloatKWindowGlobal
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/10/13 19:42
 * @Version 1.0
 */
interface IFloatKWindowGlobal<T> {
    fun isAttachedToWindowManager():Boolean
    fun setWindowParams(layoutParams: LayoutParams): T
    fun setWindowParams(block: IExt_Listener<WindowManager.LayoutParams>): T
    fun setDagger(dragger: IFloatKWindowDragger): T
}