package com.mozhimen.floatk.basic.helpers

import android.util.Log
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import com.mozhimen.kotlin.elemk.androidx.lifecycle.LifecycleOwnerProxy
import com.mozhimen.kotlin.lintk.optins.OApiInit_ByLazy

/**
 * @ClassName SavedStateRegistryOwnerProxy
 * @Description TODO
 * @Author mozhimen
 * @Date 2024/9/12
 * @Version 1.0
 */
@OApiInit_ByLazy
class FloatKOwnerProxy : SavedStateRegistryOwner, LifecycleOwnerProxy(), ViewModelStoreOwner, OnBackPressedDispatcherOwner {
    protected val savedStateRegistryController = SavedStateRegistryController.create(this)
    private var mOnBackPressedDispatcher: OnBackPressedDispatcher? = null
    private var mViewModelStore: ViewModelStore? = null

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry
    override val viewModelStore: ViewModelStore
        get() {
            ensureViewModelStore()
            return mViewModelStore!!
        }
    override val onBackPressedDispatcher: OnBackPressedDispatcher
        get() {
            if (mOnBackPressedDispatcher == null) {
                mOnBackPressedDispatcher = OnBackPressedDispatcher(Runnable {
                    try {

                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e(TAG, "OnBackPressedDispatcher: ${e.message}")
                    }/*catch (e: IllegalStateException) {
                        if (!TextUtils.equals(e.message, "Can not perform this action after onSaveInstanceState")) {
                            Log.e(TAG, "OnBackPressedDispatcher: ${e.message}")
                        }
                    } catch (e: NullPointerException) {
                        if (!TextUtils.equals(e.message,
                                "Attempt to invoke virtual method 'android.os.Handler "
                                        + "android.app.FragmentHostCallback.getHandler()' on a "
                                        + "null object reference")
                        ) {
                            Log.e(TAG, "OnBackPressedDispatcher: ${e.message}")
                        }
                    }*/
                })
            }
            return mOnBackPressedDispatcher!!
        }

    //////////////////////////////////////////////////////////////////////////

    init {
        savedStateRegistryController.performAttach()
    }

    //////////////////////////////////////////////////////////////////////////

    override fun onCreate(name: String) {
        savedStateRegistryController.performRestore(null)
        super.onCreate(name)
    }

    override fun onDestroy(name: String) {
        viewModelStore.clear()
        super.onDestroy(name)
    }

    //////////////////////////////////////////////////////////////////////////

    private fun ensureViewModelStore() {
        if (mViewModelStore == null) {
            mViewModelStore = ViewModelStore()
        }
    }
}