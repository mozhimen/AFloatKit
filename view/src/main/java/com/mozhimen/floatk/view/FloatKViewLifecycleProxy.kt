package com.mozhimen.floatk.view

import android.app.Activity
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.mozhimen.basick.bases.BaseWakeBefDestroyLifecycleObserver
import com.mozhimen.kotlin.elemk.commons.IExt_AListener
import com.mozhimen.kotlin.lintk.optins.OApiCall_BindLifecycle
import com.mozhimen.kotlin.lintk.optins.OApiCall_BindViewLifecycle
import com.mozhimen.kotlin.lintk.optins.OApiInit_ByLazy
import com.mozhimen.kotlin.utilk.android.util.UtilKLogWrapper

/**
 * @ClassName FloatKViewProxy
 * @Description TODO
 * @Author mozhimen
 * @Date 2025/2/5
 * @Version 1.0
 */
@OApiCall_BindViewLifecycle
@OApiInit_ByLazy
@OApiCall_BindLifecycle
class FloatKViewLifecycleProxy(private var _activity: Activity?) : BaseWakeBefDestroyLifecycleObserver() {

    private val _floatKView: FloatKView by lazy { FloatKView.instance }

    ////////////////////////////////////////////////////////////////////////

    fun init(block: IExt_AListener<FloatKView, FloatKView>): FloatKView {
        //        EasyFloat
        //            .layout(R.layout.layout_float_view)
        //            .blackList(mutableListOf(ThirdActivity::class.java))
        //            .layoutParams(initLayoutParams())
        //            .dragEnable(true)
        //            .setAutoMoveToEdge(true)
        //            .listener {
        //                initListener(it)
        //            }
        //            .show(this)
        return _floatKView.block()
    }

    fun show() {
        if (_activity != null) {
            _floatKView.show(_activity!!)
        } else {
            UtilKLogWrapper.e(TAG, "show: _activity != null && !_isShow ${_activity != null} && ${!_floatKView.isRegisterActivityLifecycleCallbacks()}")
        }
    }

    fun getRoot(): ViewGroup? =
        if (_floatKView.isRegisterActivityLifecycleCallbacks())
            _floatKView.getRoot()
        else null

    fun dismiss() {
        if (_activity != null) {
            _floatKView.dismiss(_activity!!)
        }
    }

    ////////////////////////////////////////////////////////////////////////

    override fun onDestroy(owner: LifecycleOwner) {
        dismiss()
        _activity = null
        super.onDestroy(owner)
    }
}