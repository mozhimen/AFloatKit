package com.mozhimen.floatk.view

import android.annotation.SuppressLint
import android.widget.FrameLayout
import com.mozhimen.floatk.basic.bases.BaseFloatK
import com.mozhimen.floatk.view.commons.IFloatKView
import com.mozhimen.kotlin.lintk.optins.OApiInit_ByLazy

@SuppressLint("StaticFieldLeak")
class FloatKView : BaseFloatK<FloatKView>(),IFloatKView<FloatKView> {
    companion object {
        @JvmStatic
        val instance = INSTANCE.holder
    }

    private object INSTANCE {
        val holder = FloatKView()
    }

    ///////////////////////////////////////////////////////////////////////////////////

    @OptIn(OApiInit_ByLazy::class)
    private val _floatKProxy by lazy { FloatKViewProxy() }

    ///////////////////////////////////////////////////////////////////////////////////

    @OptIn(OApiInit_ByLazy::class)
    override fun getFloatKProxy(): FloatKViewProxy {
        return _floatKProxy
    }

    @OptIn(OApiInit_ByLazy::class)
    override fun attach(container: FrameLayout?): FloatKView {
        _floatKProxy.attach(container)
        return this
    }

    @OptIn(OApiInit_ByLazy::class)
    override fun detach(container: FrameLayout?): FloatKView {
        _floatKProxy.detach(container)
        return this
    }
}