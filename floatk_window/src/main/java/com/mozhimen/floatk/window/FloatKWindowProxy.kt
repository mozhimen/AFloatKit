package com.mozhimen.floatk.window

import android.app.Activity
import android.content.Context
import android.graphics.PixelFormat
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
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
import com.mozhimen.floatk.basic.commons.IFloatKProxy
import com.mozhimen.floatk.basic.helpers.FloatKOwnerProxy
import com.mozhimen.floatk.window.commons.IFloatKWindow
import com.mozhimen.floatk.window.commons.IFloatKWindowDragger
import com.mozhimen.floatk.window.impls.FloatKWindowDaggerCommon
import com.mozhimen.floatk.window.widgets.LayoutKFrameTouchWindow
import com.mozhimen.kotlin.elemk.commons.IExt_Listener
import com.mozhimen.kotlin.lintk.optins.OApiInit_ByLazy
import com.mozhimen.kotlin.utilk.android.app.getDecorView
import com.mozhimen.kotlin.utilk.android.app.isFinishingOrDestroyed
import com.mozhimen.kotlin.utilk.android.util.UtilKLogWrapper
import com.mozhimen.kotlin.utilk.android.view.addAndRemoveOnGlobalLayoutListener
import com.mozhimen.kotlin.utilk.android.view.addViewSafe
import com.mozhimen.kotlin.utilk.android.view.removeViewSafe
import com.mozhimen.kotlin.utilk.bases.BaseUtilK
import java.lang.ref.WeakReference
import kotlin.properties.Delegates

/**
 * @ClassName BaseEasyFloatProxy
 * @Description TODO
 * @Author mozhimen
 * @Date 2024/10/12
 * @Version 1.0
 */
@OApiInit_ByLazy
class FloatKWindowProxy : IFloatKProxy, IFloatKWindow<Unit>, BaseUtilK(),
    LayoutKFrameTouchWindow.OnPositionChangedListener {
    @LayoutRes
    private var _layoutId = 0 //R.layout.en_floating_view;
    private var _layout: View? = null
    private var _dragEnable: Boolean = true
    private var _autoMoveToEdge: Boolean = true
    private var _layoutParams: ViewGroup.LayoutParams = getDefaultFrameLayoutLayoutParams()
    private var _windowParams: WindowManager.LayoutParams = getDefaultWindowManagerLayoutParams()
    private var _floatKWindowDragger: IFloatKWindowDragger? = null

    private val _floatKOwnerProxy: FloatKOwnerProxy by lazy { FloatKOwnerProxy() }

    ////////////////////////////////////////////////////////

    private var _windowManagerRefs: HashMap<String, WeakReference<WindowManager>> = HashMap()
    private var _currentClassName: String = ""
    private var _windowParamsRef: WindowManager.LayoutParams? = null
    private var _layoutKRoot: LayoutKFrameTouchWindow? by Delegates.observable(null) { property, oldValue, newValue ->
        if (newValue != null) {
            _floatKOwnerProxy.onStart(NAME)
        } else {
            _floatKOwnerProxy.onStop(NAME)
        }
    }

    ////////////////////////////////////////////////////////

    init {
        _floatKOwnerProxy.onCreate(this.NAME)
    }

    ////////////////////////////////////////////////////////

    override fun getWindowManagerRefs(): Map<String, WeakReference<WindowManager>> {
        return _windowManagerRefs
    }

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
        return _layoutKRoot
    }

    //////////////////////////////////////////////////

    override fun add(context: Context) {
        if (_layoutKRoot == null) {
            _layoutKRoot = if (_layout != null) {
                LayoutKFrameTouchWindow(context, _layout!!)
            } else {
                LayoutKFrameTouchWindow(context, _layoutId)
            }.apply {
                setDragEnable(_dragEnable)
                setAutoMoveToEdge(_autoMoveToEdge)
                setOnPositionChangedListener(this@FloatKWindowProxy)
                layoutParams = _layoutParams
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
    }

    override fun remove() {
        if (_layoutKRoot != null) {
            _windowManagerRefs.filterValues { it.get() != null }.entries.forEach {
                it.value.get()?.removeViewSafe(_layoutKRoot!!)
            }
            _windowManagerRefs.clear()
            _layoutKRoot = null
        }
        _windowParamsRef = null
        if (_currentClassName.isNotEmpty()) {
            _currentClassName = ""
        }
    }

    override fun attach(activity: Activity) {
        if (_layoutKRoot == null) {
            Log.w(TAG, "attach: _layoutKRoot == null generate")
            add(_context)
        }

        val windowManagerLast: WindowManager? = _windowManagerRefs[_currentClassName]?.get()
        if (windowManagerLast != null) {
            UtilKLogWrapper.d(TAG, "attach: removeViewSafe windowManager $windowManagerLast")
            windowManagerLast.removeViewSafe(_layoutKRoot!!)
            UtilKLogWrapper.d(TAG, "attach: removeViewSafe after _layoutKRoot.parent ${_layoutKRoot!!.parent}")
        }

        val windowManagerCurr: WindowManager = activity.windowManager
        _currentClassName = activity.javaClass.name
        UtilKLogWrapper.d(TAG, "attach: _currentClassName $_currentClassName")
        if (!_windowManagerRefs.containsKey(_currentClassName)) {
            _windowManagerRefs[_currentClassName] = WeakReference(windowManagerCurr)
        }
        UtilKLogWrapper.d(TAG, "attach: addViewSafe windowManager $windowManagerCurr activity $activity ")
        activity.getDecorView<View>().addAndRemoveOnGlobalLayoutListener {
            var res = windowManagerCurr.addViewSafe(
                _layoutKRoot!!,
                WindowManager.LayoutParams().apply {
                    generateWindowManagerParams(this)
                }.also {
                    _windowParamsRef = it
                })
            if (!res) {
                _layoutKRoot!!.postDelayed({
                    if (_layoutKRoot!!.parent==null&&!activity.isFinishingOrDestroyed()){
                        res = windowManagerCurr.addViewSafe(
                            _layoutKRoot!!,
                            WindowManager.LayoutParams().apply {
                                generateWindowManagerParams(this)
                            }.also {
                                _windowParamsRef = it
                            })
                        Log.d(TAG, "attach: retry res $res")
                    }
                },1000)
            }
        }
    }

    private fun generateWindowManagerParams(windowParams: WindowManager.LayoutParams) {
        windowParams.apply {
            width = _windowParams.width
            height = _windowParams.height
            type = _windowParams.type
            flags = _windowParams.flags
            format = _windowParams.format
            gravity = _windowParams.gravity
            x = _windowParams.x
            y = _windowParams.y
        }
    }

    override fun detach(activity: Activity) {
        val className = activity.javaClass.name
        val windowManagerLast = _windowManagerRefs[className]?.get()
        if (_layoutKRoot != null && windowManagerLast != null) {
            UtilKLogWrapper.w(TAG, "detach: removeViewSafe windowManager ${activity.windowManager} activity $activity ")
            activity.windowManager.removeViewSafe(_layoutKRoot!!)
            _windowManagerRefs.remove(className)
        }
        if (_windowParamsRef != null) {
            _windowParams = _windowParamsRef!!
        }
//        //
//        UtilKLogWrapper.w(TAG, "detach: _currentClassName $_currentClassName")
//        val windowManagerCurr: WindowManager? = _windowManagerRefs[_currentClassName]?.get()
//        if (windowManagerCurr!=null){
//            UtilKLogWrapper.w(TAG, "detach: addViewSafe windowManager ${activity.windowManager}")
//            windowManagerCurr.addViewSafe(
//                _layoutKRoot!!,
//                _windowParams/*activity.window.attributes*//*WindowManager.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)*//*.apply {
//                generateWindowManagerParams(this)
//            }*/.also {
//                    _windowParamsRef = it
//                })
//        }
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
        _layoutKRoot?.layoutParams = layoutParams
    }

    override fun setLayoutParams(block: IExt_Listener<ViewGroup.LayoutParams>) {
        _layoutParams.block()
        setLayoutParams(_layoutParams)
    }

    override fun setDragEnable(dragEnable: Boolean) {
        _dragEnable = dragEnable
        _layoutKRoot?.setDragEnable(dragEnable)
        if (_floatKWindowDragger == null) {
            _floatKWindowDragger = FloatKWindowDaggerCommon()
        }
    }

    override fun setAutoMoveToEdge(autoMoveToEdge: Boolean) {
        _autoMoveToEdge = autoMoveToEdge
        _layoutKRoot?.setAutoMoveToEdge(_autoMoveToEdge)
    }

    override fun setWindowParams(block: IExt_Listener<WindowManager.LayoutParams>) {
        _windowParams.block()
        _windowParamsRef?.apply {
            block()
            onPositionChanged(this.x.toFloat(), this.y.toFloat())
        }
    }

    override fun setWindowParams(layoutParams: WindowManager.LayoutParams) {
        _windowParams = layoutParams
        _windowParamsRef = layoutParams
    }

    override fun setDagger(dragger: IFloatKWindowDragger) {
        _floatKWindowDragger = dragger
    }

    ////////////////////////////////////////////////////////

    override fun onPositionChanged(x: Float, y: Float) {
//        Log.d(
//            TAG,
//            "onPositionChanged: x y $x $y _floatKWindowDragger != null ${_floatKWindowDragger != null} _windowManagerRef?.get() != null ${
//                _windowManagerRefs.get(_currentClassName)?.get() != null
//            } _windowParamsRef != null ${_windowParamsRef != null}  _layoutKRoot != null ${_layoutKRoot != null}"
//        )
        if (_floatKWindowDragger != null && _windowManagerRefs.get(_currentClassName)?.get() != null && _windowParamsRef != null && _layoutKRoot != null) {
            _windowParamsRef!!.x = x.toInt()
            _windowParamsRef!!.y = y.toInt()
            UtilKLogWrapper.d(TAG, "onPositionChanged: x.toInt() $x y.toInt() $y")
            _floatKWindowDragger?.update(_windowManagerRefs.get(_currentClassName)!!.get()!!, _layoutKRoot!!, _windowParamsRef!!)
        }
    }

    ////////////////////////////////////////////////////////

    companion object {
        fun getDefaultFrameLayoutLayoutParams(): FrameLayout.LayoutParams {
            val layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutParams.gravity = Gravity.TOP or Gravity.START
            return layoutParams
        }

        fun getDefaultWindowManagerLayoutParams(): WindowManager.LayoutParams {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL
            // 设置触摸外层布局（除 WindowManager 外的布局，默认是 WindowManager 显示的时候外层不可触摸）
            // 需要注意的是设置了 FLAG_NOT_TOUCH_MODAL 必须要设置 FLAG_NOT_FOCUSABLE，否则就会导致用户按返回键无效
//        layoutParams.flags = 263208
            layoutParams.flags = /*WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or*/  WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            layoutParams.format = PixelFormat.TRANSLUCENT//1->RGB
            layoutParams.gravity = Gravity.LEFT or Gravity.TOP//51
//            layoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            return layoutParams
        }
    }
}