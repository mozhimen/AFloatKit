package com.mozhimen.floatk.test

import android.animation.ValueAnimator
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.compositionContext
import androidx.compose.ui.unit.dp
import com.mozhimen.bindk.bases.viewbinding.activity.BaseActivityVB
import com.mozhimen.floatk.test.databinding.ActivityMainBinding
import com.mozhimen.floatk.window.FloatKWindow
import com.mozhimen.floatk.window.FloatKWindowProxy
import com.mozhimen.kotlin.lintk.optins.OApiInit_ByLazy
import com.mozhimen.kotlin.utilk.android.content.startContext
import com.mozhimen.kotlin.utilk.android.util.dp2px
import com.mozhimen.kotlin.utilk.android.util.dp2pxI
import com.mozhimen.kotlin.utilk.android.widget.showToast
import com.mozhimen.kotlin.utilk.commons.IUtilK
import com.mozhimen.kotlin.utilk.wrapper.UtilKScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivityWindow : BaseActivityVB<ActivityMainBinding>(), IUtilK {

    fun showSimple(view: View) {
        if (FloatKWindow.instance.isRegisterActivityLifecycleCallbacks())
            return
        FloatKWindow.instance
            .setCustomView(R.layout.layout_float_view)
            .addBlackList(mutableListOf(ThirdActivity::class.java))
            .setLayoutParams(getLayoutParamsDefault())
            .setDragEnable(true)
            .setAutoMoveToEdge(true)
            .show(this)
        FloatKWindow.instance.getRoot()?.let {
            initListener(it)
        }
    }

    @OptIn(OApiInit_ByLazy::class)
    private fun getLayoutParamsDefault(): FrameLayout.LayoutParams {
        val params = FloatKWindowProxy.getDefaultFrameLayoutLayoutParams()
//        params.setMargins(params.leftMargin, 100.dp2pxI(), params.rightMargin, params.bottomMargin)//设置初始化位置(但是设置MATCH_PARENT不要通过这种方式, 会使全屏不完整)
        params.setMargins(0, params.topMargin, params.rightMargin, 500)
        return params
    }

    ///////////////////////////////////////////////////////////////////////

    fun showCompose(view: View) {
        if (FloatKWindow.instance.isRegisterActivityLifecycleCallbacks())
            return
        FloatKWindow.instance
            .setCustomView(
                getComposeView()
            )
            .addBlackList(mutableListOf(ThirdActivity::class.java))
            .setLayoutParams(getLayoutParamsCompose())
            .setDragEnable(true)
            .setAutoMoveToEdge(true)
            .show(this)
    }

    private fun getLayoutParamsCompose(): FrameLayout.LayoutParams {
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        params.gravity = Gravity.TOP or Gravity.START
        return params
    }

    private fun getComposeView(): ComposeView {
        val coroutineContext = AndroidUiDispatcher.CurrentThread
        val coroutineScope = CoroutineScope(coroutineContext)
        val reRecomposer = Recomposer(coroutineContext)
        coroutineScope.launch {
            reRecomposer.runRecomposeAndApplyChanges()
        }//如果使用compose, 一定要自己构建重组器, 不然reRecomposer detach from viewTree使点击事件无效
        return ComposeView(this@MainActivityWindow).apply {
            compositionContext = reRecomposer
            setContent {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.Black)
                ) {
                    Text(
                        text = "Android",
                        modifier = Modifier.clickable {
                            "Hello".showToast()
                        }
                    )
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////

    //高阶用法, 可以折叠展开成全屏的遮罩悬浮窗, 可设置初始位置
    fun showResizeFullScreen(view: View) {
        if (FloatKWindow.instance.isRegisterActivityLifecycleCallbacks())
            return
        FloatKWindow.instance
            .setCustomView(
                getComposeView2()
            )
            .addBlackList(mutableListOf(ThirdActivity::class.java))
            .setLayoutParams(getLayoutParamsFullScreen())
            .setWindowParams {
                y = 50.dp2pxI()//或者通过setInitMargin
            }
            .setDragEnable(true)
            .setAutoMoveToEdge(true)
            .show(this)
    }

    private fun getLayoutParamsFullScreen(): FrameLayout.LayoutParams {
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        params.gravity = Gravity.TOP or Gravity.START
        return params
    }

    private fun getComposeView2(): ComposeView {
        val coroutineContext = AndroidUiDispatcher.CurrentThread
        val coroutineScope = CoroutineScope(coroutineContext)
        val reRecomposer = Recomposer(coroutineContext)
        coroutineScope.launch {
            reRecomposer.runRecomposeAndApplyChanges()
        }//如果使用compose, 一定要自己构建重组器, 不然reRecomposer detach from viewTree使点击事件无效
        return ComposeView(this@MainActivityWindow).apply {
            compositionContext = reRecomposer
            setContent {
                var isFold by remember {
                    mutableStateOf(true)
                }
                if (isFold) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color.DarkGray)
                    ) {
                        Text(
                            text = "Unfold",
                            color = Color.White,
                            modifier = Modifier.clickable {
                                FloatKWindow.instance.setLayoutParams {
                                    width = ViewGroup.LayoutParams.WRAP_CONTENT
                                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                                }
                                isFold = false
                            }
                        )
                        Text(
                            text = "Say Hello",
                            color = Color.White,
                            modifier = Modifier.clickable {
                                "Hello".showToast()
                            }
                        )
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .background(Color.DarkGray)
                    ) {
                        Text(
                            text = "Fold",
                            color = Color.White,
                            modifier = Modifier
                                .clickable {
                                    FloatKWindow.instance.setLayoutParams {
                                        width = UtilKScreen.getWidth()
                                        height = UtilKScreen.getHeight()
                                    }
                                    isFold = true
                                }
                        )
                        Text(
                            text = "Say Hello",
                            color = Color.White,
                            modifier = Modifier
                                .clickable {
                                    "Hello".showToast()
                                }
                        )
                    }
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////

    fun dismiss(view: View) {
        FloatKWindow.instance.dismiss(this)
    }

    ///////////////////////////////////////////////////////////////////////

    fun startSecond(view: View) {
        startContext<SecondActivity>()
    }

    fun startThird(view: View) {
        startContext<ThirdActivity>()
    }

    fun startForth(view: View) {
        startContext<ForthActivity>()
    }

    fun getAllWindowManagers(view: View) {
        Log.d(TAG, "getAllWindowManagers: getWindowManagerRefs ${FloatKWindow.instance.getWindowManagerRefs().map { it.value.get() }}")
    }

    ///////////////////////////////////////////////////////////////////////

    private fun initListener(root: View?) {
        val rootView = root?.findViewById<View>(R.id.ll_root)
        root?.findViewById<View>(R.id.floating_ball)?.setOnClickListener {
            if (rootView != null) {
                if (rootView.getTag(R.id.animate_type) == null) {
                    rootView.setTag(R.id.animate_type, true)
                }
                val isCollapse = rootView.getTag(R.id.animate_type) as Boolean
                animScale(rootView, isCollapse)
                rootView.setTag(R.id.animate_type, isCollapse.not())
            }
        }
        root?.findViewById<View>(R.id.floating_ball_one)?.setOnClickListener {
            Toast.makeText(this, "click", Toast.LENGTH_SHORT).show()
        }
    }

    private fun animScale(view: View, isCollapse: Boolean) {
        val start = if (isCollapse) 172f.dp2px() else 60f.dp2px()
        val end = if (isCollapse) 60f.dp2px() else 172f.dp2px()

        val scaleBig = ValueAnimator.ofFloat(start, end)
        scaleBig.duration = 1000
        scaleBig.addUpdateListener {
            val layoutParams = view.layoutParams
            layoutParams.height = (it.animatedValue as Float).toInt()
            view.layoutParams = layoutParams
        }
        scaleBig.start()
    }
}