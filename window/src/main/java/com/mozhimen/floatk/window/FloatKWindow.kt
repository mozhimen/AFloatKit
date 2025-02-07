package com.mozhimen.floatk.window

import android.app.Activity
import android.util.Log
import android.view.WindowManager
import com.mozhimen.floatk.basic.bases.BaseFloatK
import com.mozhimen.floatk.window.commons.IFloatKWindow
import com.mozhimen.floatk.window.commons.IFloatKWindowDragger
import com.mozhimen.kotlin.elemk.commons.IExt_Listener
import com.mozhimen.kotlin.lintk.optins.OApiInit_ByLazy
import java.lang.ref.WeakReference

/**
 * @ClassName FloatKWindow
 * @Description TODO
 * @Author mozhimen
 * @Date 2024/10/12
 * @Version 1.0
 */
class FloatKWindow : BaseFloatK<FloatKWindow>(), IFloatKWindow<FloatKWindow> {
    companion object {
        @JvmStatic
        val instance = INSTANCE.holder
    }

    private object INSTANCE {
        val holder = FloatKWindow()
    }

    ///////////////////////////////////////////////////////////////////////////////////

    @OptIn(OApiInit_ByLazy::class)
    private val _floatProxy by lazy { FloatKWindowDelegate() }

    ///////////////////////////////////////////////////////////////////////////////////

    @OptIn(OApiInit_ByLazy::class)
    override fun getWindowManagerRefs(): Map<String, WeakReference<WindowManager>> {
        return _floatProxy.getWindowManagerRefs()
    }

    @OptIn(OApiInit_ByLazy::class)
    override fun getFloatKProxy(): FloatKWindowDelegate {
        return _floatProxy
    }

    @OptIn(OApiInit_ByLazy::class)
    override fun setWindowParams(layoutParams: WindowManager.LayoutParams): FloatKWindow {
        _floatProxy.setWindowParams(layoutParams)
        return this
    }

    @OptIn(OApiInit_ByLazy::class)
    override fun setWindowParams(block: IExt_Listener<WindowManager.LayoutParams>): FloatKWindow {
        _floatProxy.setWindowParams(block)
        return this
    }

    @OptIn(OApiInit_ByLazy::class)
    override fun setDagger(dragger: IFloatKWindowDragger): FloatKWindow {
       _floatProxy.setDagger(dragger)
        return this
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
        if (isActivityInValid(activity)) return
        Log.d(TAG, "onActivityResumed: activity $activity")
        attach(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        if (isActivityInValid(activity)) return
        Log.d(TAG, "onActivityPaused: activity $activity")
        detach(activity)
    }
}