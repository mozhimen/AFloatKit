package com.mozhimen.floatk.basic.commons

import android.app.Activity
import android.content.Context
import android.graphics.RectF
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.lifecycle.LifecycleOwner
import com.mozhimen.kotlin.elemk.commons.IExt_Listener

/**
 * @ClassName IEasyFloat
 * @Description TODO
 * @Author mozhimen
 * @Date 2024/9/11
 * @Version 1.0
 */
interface IFloatK<T> {
    fun getLayout(): View?

    fun getLayoutId(): Int

    fun getRoot(): ViewGroup?

    fun getLifecycleOwner(): LifecycleOwner

    //////////////////////////////////////////////////

    fun add(context: Context): T

    fun remove(): T

    fun attach(activity: Activity): T

    fun detach(activity: Activity): T

    //////////////////////////////////////////////////

    fun setCustomView(view: View): T

    fun setCustomView(@LayoutRes intLayoutId: Int): T

    fun setLayoutParams(layoutParams: ViewGroup.LayoutParams): T

    fun setLayoutParams(block: IExt_Listener<ViewGroup.LayoutParams>): T

    fun setDragEnable(dragEnable: Boolean): T

    fun setAutoMoveToEdge(autoMoveToEdge: Boolean): T

//    fun setInitMargin(margin: RectF): T
}