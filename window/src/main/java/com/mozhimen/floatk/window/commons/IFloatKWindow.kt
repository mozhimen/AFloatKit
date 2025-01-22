package com.mozhimen.floatk.window.commons

import android.view.ViewGroup
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import com.mozhimen.kotlin.elemk.commons.IExt_Listener
import java.lang.ref.WeakReference

/**
 * @ClassName IEasyFloat
 * @Description TODO
 * @Author mozhimen
 * @Date 2024/9/11
 * @Version 1.0
 */
interface IFloatKWindow<T> {
    fun getWindowManagerRefs(): Map<String, WeakReference<WindowManager>>
    fun setWindowParams(layoutParams: LayoutParams): T
    fun setWindowParams(block: IExt_Listener<WindowManager.LayoutParams>): T
    fun setDagger(dragger: IFloatKWindowDragger): T
}