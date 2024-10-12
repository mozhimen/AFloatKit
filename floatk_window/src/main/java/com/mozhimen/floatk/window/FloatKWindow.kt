package com.mozhimen.floatk.window

import android.view.WindowManager
import com.mozhimen.floatk.basic.bases.BaseFloatK
import com.mozhimen.floatk.window.commons.IFloatKWindow
import com.mozhimen.floatk.window.commons.IFloatKWindowDragger
import com.mozhimen.kotlin.lintk.optins.OApiInit_ByLazy

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
    private val _floatProxy by lazy { FloatKWindowProxy() }

    ///////////////////////////////////////////////////////////////////////////////////

    @OptIn(OApiInit_ByLazy::class)
    override fun getFloatKProxy(): FloatKWindowProxy {
        return _floatProxy
    }

    @OptIn(OApiInit_ByLazy::class)
    override fun setWindowParams(layoutParams: WindowManager.LayoutParams): FloatKWindow {
        _floatProxy.setWindowParams(layoutParams)
        return this
    }

    @OptIn(OApiInit_ByLazy::class)
    override fun setDagger(dragger: IFloatKWindowDragger): FloatKWindow {
       _floatProxy.setDagger(dragger)
        return this
    }
}