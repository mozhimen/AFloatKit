package com.mozhimen.floatk.window.global

import android.app.Activity
import android.util.Log
import android.view.WindowManager
import com.mozhimen.floatk.basic.bases.BaseFloatK
import com.mozhimen.floatk.window.commons.IFloatKWindowDragger
import com.mozhimen.floatk.window.global.commons.IFloatKWindowGlobal
import com.mozhimen.kotlin.elemk.commons.IExt_Listener
import com.mozhimen.kotlin.lintk.optins.OApiInit_ByLazy
import com.mozhimen.kotlin.lintk.optins.permission.OPermission_SYSTEM_ALERT_WINDOW
import com.mozhimen.kotlin.utilk.wrapper.UtilKPermission
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @ClassName FloatKWindowGlobal
 * @Description TODO
 * @Author Mozhimen / Kolin Zhao
 * @Date 2024/10/13 19:40
 * @Version 1.0
 */
@OptIn(OPermission_SYSTEM_ALERT_WINDOW::class)
class FloatKWindowGlobal : BaseFloatK<FloatKWindowGlobal>(), IFloatKWindowGlobal<FloatKWindowGlobal> {
    companion object {
        @JvmStatic
        val instance = INSTANCE.holder
    }

    private object INSTANCE {
        val holder = FloatKWindowGlobal()
    }

    ///////////////////////////////////////////////////////////////////////////////////

    @OptIn(OApiInit_ByLazy::class)
    private val _floatProxy by lazy { FloatKWindowGlobalProxy() }
    private val _actClazzNames = CopyOnWriteArrayList<String>()

    ///////////////////////////////////////////////////////////////////////////////////

    @OptIn(OApiInit_ByLazy::class)
    override fun getFloatKProxy(): FloatKWindowGlobalProxy {
        return _floatProxy
    }

    @OptIn(OApiInit_ByLazy::class)
    override fun setWindowParams(layoutParams: WindowManager.LayoutParams): FloatKWindowGlobal {
        _floatProxy.setWindowParams(layoutParams)
        return this
    }

    @OptIn(OApiInit_ByLazy::class)
    override fun setWindowParams(block: IExt_Listener<WindowManager.LayoutParams>): FloatKWindowGlobal {
        _floatProxy.setWindowParams(block)
        return this
    }

    @OptIn(OApiInit_ByLazy::class)
    override fun setDagger(dragger: IFloatKWindowDragger): FloatKWindowGlobal {
        _floatProxy.setDagger(dragger)
        return this
    }

    override fun show(activity: Activity) {
        if (!UtilKPermission.hasSystemAlertWindow()) return
        Log.d(TAG, "show: activity $activity")
        attach(activity)
        registerActivityLifecycleCallbacks(activity.application)
    }

    override fun dismiss(activity: Activity) {
        unregisterActivityLifecycleCallbacks(activity.application)
        detach(activity)
        remove()
    }


    @OptIn(OApiInit_ByLazy::class)
    override fun isAttachedToWindowManager(): Boolean {
        return _floatProxy.isAttachedToWindowManager()
    }

    override fun onActivityStarted(activity: Activity) {
        if (!_actClazzNames.contains(activity.javaClass.name)){
            _actClazzNames.add(activity.javaClass.name)
        }
        if (!isAttachedToWindowManager()&&UtilKPermission.hasSystemAlertWindow()&&!_blackList.contains(activity.javaClass)){
            attach(activity)
        }
        if (_blackList.contains(activity.javaClass)&&isAttachedToWindowManager()){
            detach(activity)
        }
    }

    override fun onActivityStopped(activity: Activity) {
        if (_actClazzNames.contains(activity.javaClass.name)){
            _actClazzNames.remove(activity.javaClass.name)
        }
        if (_actClazzNames.isEmpty()&&isAttachedToWindowManager()){
            detach(activity)
        }
    }
}