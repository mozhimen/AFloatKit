package com.mozhimen.floatk.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.mozhimen.bindk.bases.viewbinding.activity.BaseActivityVB
import com.mozhimen.floatk.test.databinding.ActivitySecondBinding
import com.mozhimen.floatk.window.FloatKWindow

class SecondActivity : BaseActivityVB<ActivitySecondBinding>() {
    fun getAllWindowManagers(view: View) {
        Log.d(TAG, "getAllWindowManagers: getWindowManagerRefs ${FloatKWindow.instance.getWindowManagerRefs().map { it.value.get() }}")
    }
}