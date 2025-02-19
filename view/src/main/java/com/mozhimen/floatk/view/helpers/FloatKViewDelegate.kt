package com.mozhimen.floatk.view.helpers

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.activity.findViewTreeOnBackPressedDispatcherOwner
import androidx.activity.setViewTreeOnBackPressedDispatcherOwner
import androidx.annotation.LayoutRes
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.findViewTreeSavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.mozhimen.floatk.basic.commons.IFloatKDelegate
import com.mozhimen.floatk.basic.helpers.FloatKOwnerProxy
import com.mozhimen.floatk.view.commons.IFloatKView
import com.mozhimen.kotlin.elemk.commons.IExt_Listener
import com.mozhimen.kotlin.lintk.optins.OApiInit_ByLazy
import com.mozhimen.kotlin.utilk.android.app.getContentView
import com.mozhimen.kotlin.utilk.android.view.addAndRemoveOnGlobalLayoutListener
import com.mozhimen.kotlin.utilk.android.view.addViewSafe
import com.mozhimen.kotlin.utilk.android.view.removeViewSafe
import com.mozhimen.kotlin.utilk.android.view.removeView_ofParent
import com.mozhimen.xmlk.layoutk.magnet.LayoutKMagnet
import kotlin.properties.Delegates

/**
 * @ClassName BaseEasyFloatProxy
 * @Description TODO
 * @Author mozhimen
 * @Date 2024/10/12
 * @Version 1.0
 */
@OApiInit_ByLazy
class FloatKViewDelegate : IFloatKDelegate, IFloatKView<Unit> {
    @LayoutRes
    private var _layoutId = 0 //R.layout.en_floating_view;
    private var _layout: View? = null
    private var _layoutParams: ViewGroup.LayoutParams = getDefaultLayoutParams()

    private var _dragEnable = true
    private var _autoMoveToEdge = true
    private val _floatKOwnerProxy: FloatKOwnerProxy by lazy { FloatKOwnerProxy() }

    ////////////////////////////////////////////////////////

    protected var _layoutKMagnet: LayoutKMagnet? by Delegates.observable(null) { property, oldValue, newValue ->
        if (newValue != null) {
            _floatKOwnerProxy.onStart()
        } else {
            _floatKOwnerProxy.onStop()
        }
    }

    ////////////////////////////////////////////////////////

    init {
        _floatKOwnerProxy.onCreate()
    }

    ////////////////////////////////////////////////////////

    override fun getLifecycleOwner(): LifecycleOwner {
        return _floatKOwnerProxy
    }

    override fun getLayoutId(): Int {
        return _layoutId
    }

    override fun getLayout(): View? {
        return _layout
    }

    override fun getRoot(): ViewGroup? {
        return _layoutKMagnet
    }

    //////////////////////////////////////////////////

    override fun add(context: Context) {
        if (_layoutKMagnet != null) return
        _layoutKMagnet = if (_layout != null) {
            LayoutKMagnet(context, _layout!!)
        } else {
            LayoutKMagnet(context, _layoutId)
        }.apply {
            layoutParams = _layoutParams
            setDragEnable(_dragEnable)
            setAutoMoveToEdge(_autoMoveToEdge)
            if (findViewTreeLifecycleOwner() == null) {
                setViewTreeLifecycleOwner(_floatKOwnerProxy)
            }
            if (findViewTreeSavedStateRegistryOwner() == null) {
                setViewTreeSavedStateRegistryOwner(_floatKOwnerProxy)
            }
            if (findViewTreeViewModelStoreOwner() == null) {
                setViewTreeViewModelStoreOwner(_floatKOwnerProxy)
            }
            if (findViewTreeOnBackPressedDispatcherOwner() == null) {
                setViewTreeOnBackPressedDispatcherOwner(_floatKOwnerProxy)
            }
        }
    }

    override fun remove() {
        _layoutKMagnet?.removeView_ofParent()
        _layoutKMagnet = null
    }

    override fun attach(activity: Activity) {
        Log.d(TAG, "attach: ${activity}")
        attach(activity.getContentView<View>() as? FrameLayout)//getActivityRoot(activity))
    }

    override fun attach(container: FrameLayout?) {
        if (container == null) {
            Log.w(TAG, "attach: container == null")
            return
        } else if (_layoutKMagnet == null) {
            Log.w(TAG, "attach: _layoutKMagnet == null generate")
            add(container.context)
        }
        Log.d(TAG, "attach: ")
        container.addViewSafe(_layoutKMagnet!!)
        _layoutKMagnet!!.addAndRemoveOnGlobalLayoutListener {
            _layoutKMagnet!!.bringToFront()
            _layoutKMagnet!!.updateSize()
            _layoutKMagnet!!.moveToEdge()
        }
    }

    override fun detach(activity: Activity) {
        Log.d(TAG, "detach: ${activity}")
        detach(activity.getContentView<View>() as? FrameLayout)
    }

    override fun detach(container: FrameLayout?) {
        if (_layoutKMagnet != null && container != null) {
            Log.d(TAG, "detach: _layoutKMagnet")
            container.removeViewSafe(_layoutKMagnet!!)
        }
    }

    //////////////////////////////////////////////////

    override fun setCustomView(view: View) {
        _layout = view
    }

    override fun setCustomView(intLayoutId: Int) {
        _layoutId = intLayoutId
    }

    override fun setLayoutParams(layoutParams: ViewGroup.LayoutParams) {
        _layoutParams = layoutParams
        _layoutKMagnet?.layoutParams = layoutParams
    }

    override fun setDragEnable(dragEnable: Boolean) {
        _dragEnable = dragEnable
        _layoutKMagnet?.setDragEnable(dragEnable)
    }

    override fun setAutoMoveToEdge(autoMoveToEdge: Boolean) {
        _autoMoveToEdge = autoMoveToEdge
        _layoutKMagnet?.setAutoMoveToEdge(autoMoveToEdge)
    }

    override fun setLayoutParams(block: IExt_Listener<ViewGroup.LayoutParams>) {
        _layoutParams.block()
        setLayoutParams(_layoutParams)
    }

    ////////////////////////////////////////////////////////

    companion object {
        fun getDefaultLayoutParams(): FrameLayout.LayoutParams {
            val layoutParams = FrameLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.gravity = Gravity.TOP or Gravity.START
            return layoutParams
        }
    }
}