package com.mozhimen.floatk.view.commons

import android.widget.FrameLayout

/**
 * @ClassName IEasyFloat
 * @Description TODO
 * @Author mozhimen
 * @Date 2024/9/11
 * @Version 1.0
 */
interface IFloatKView<T>  {
    fun attach(container: FrameLayout?): T

    fun detach(container: FrameLayout?): T
}