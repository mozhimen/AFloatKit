package com.mozhimen.floatk.window.commons

import android.view.WindowManager.LayoutParams

/**
 * @ClassName IEasyFloat
 * @Description TODO
 * @Author mozhimen
 * @Date 2024/9/11
 * @Version 1.0
 */
interface IFloatKWindow<T> {
    fun setWindowParams(layoutParams: LayoutParams):T
    fun setDagger(dragger: IFloatKWindowDragger):T
}