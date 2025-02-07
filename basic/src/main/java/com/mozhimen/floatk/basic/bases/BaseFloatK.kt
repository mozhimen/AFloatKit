package com.mozhimen.floatk.basic.bases

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.mozhimen.floatk.basic.commons.IFloatK
import com.mozhimen.floatk.basic.commons.IFloatKDelegate
import com.mozhimen.kotlin.elemk.android.app.bases.BaseActivityLifecycleCallbacks
import com.mozhimen.kotlin.elemk.commons.IExt_Listener
import com.mozhimen.kotlin.utilk.commons.IUtilK
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @ClassName BaseEasyFloat
 * @Description TODO
 * @Author mozhimen
 * @Date 2024/10/12
 * @Version 1.0
 */
abstract class BaseFloatK<T> : BaseActivityLifecycleCallbacks(), IFloatK<T>, IUtilK {
    protected val _blackList = mutableListOf<Class<*>>()
    protected val _isAdd = AtomicBoolean(false)

    ///////////////////////////////////////////////////////////////////////////////////

    abstract fun getFloatKDelegate(): IFloatKDelegate

    ///////////////////////////////////////////////////////////////////////////////////

    override fun getLifecycleOwner(): LifecycleOwner {
        return getFloatKDelegate().getLifecycleOwner()
    }

    override fun getRoot(): ViewGroup? {
        return getFloatKDelegate().getRoot()
    }

    override fun getLayoutId(): Int {
        return getFloatKDelegate().getLayoutId()
    }

    override fun getLayout(): View? {
        return getFloatKDelegate().getLayout()
    }

    override fun setCustomView(intLayoutId: Int): T {
        getFloatKDelegate().setCustomView(intLayoutId)
        return this as T
    }

    override fun setCustomView(view: View): T {
        getFloatKDelegate().setCustomView(view)
        return this as T
    }

    override fun setLayoutParams(layoutParams: ViewGroup.LayoutParams): T {
        getFloatKDelegate().setLayoutParams(layoutParams)
        return this as T
    }

    override fun setLayoutParams(block: IExt_Listener<ViewGroup.LayoutParams>): T {
        getFloatKDelegate().setLayoutParams(block)
        return this as T
    }

    /**
     * 是否可拖拽（位置是否固定）
     */
    override fun setDragEnable(dragEnable: Boolean): T {
        getFloatKDelegate().setDragEnable(dragEnable)
        return this as T
    }

    /**
     * 是否自动靠边
     */
    override fun setAutoMoveToEdge(autoMoveToEdge: Boolean): T {
        getFloatKDelegate().setAutoMoveToEdge(autoMoveToEdge)
        return this as T
    }

    override fun add(context: Context): T {
        getFloatKDelegate().add(context)
        return this as T
    }

    override fun remove(): T {
        getFloatKDelegate().remove()
        return this as T
    }

    override fun attach(activity: Activity): T {
        getFloatKDelegate().attach(activity)
        return this as T
    }

    override fun detach(activity: Activity): T {
        getFloatKDelegate().detach(activity)
        return this as T
    }

    ///////////////////////////////////////////////////////////////////////////////////

    fun addBlackList(blackList: MutableList<Class<*>>): T {
        _blackList.addAll(blackList)
        return this as T
    }

    fun registerActivityLifecycleCallbacks(application: Application) {
        if (_isAdd.compareAndSet(false, true)) {
            application.registerActivityLifecycleCallbacks(this)
        }
    }

    fun unregisterActivityLifecycleCallbacks(application: Application) {
        if (_isAdd.compareAndSet(true, false)) {
            application.unregisterActivityLifecycleCallbacks(this)
        }
    }

    open fun show(activity: Activity) {
        Log.d(TAG, "show: activity $activity")
        attach(activity)
        registerActivityLifecycleCallbacks(activity.application)
    }

    open fun dismiss(activity: Activity) {
        unregisterActivityLifecycleCallbacks(activity.application)
        detach(activity)
        remove()
    }

    fun isRegisterActivityLifecycleCallbacks(): Boolean =
        _isAdd.get()

    ///////////////////////////////////////////////////////////////////////////////////

    override fun onActivityStarted(activity: Activity) {
        if (isActivityInValid(activity)) return
        Log.d(TAG, "onActivityStarted: activity $activity")
        attach(activity)
    }

    override fun onActivityStopped(activity: Activity) {
        if (isActivityInValid(activity)) return
        Log.d(TAG, "onActivityStopped: activity $activity")
        detach(activity)
    }

    ///////////////////////////////////////////////////////////////////////////////////

    protected fun isActivityInValid(activity: Activity): Boolean {
        return _blackList.contains(activity::class.java)
    }
}