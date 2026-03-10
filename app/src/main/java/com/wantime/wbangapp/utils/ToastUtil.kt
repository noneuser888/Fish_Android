package com.block.dog.common.util

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import com.wantime.wbangapp.utils.Constants

/**
 *  on 2019/3/13.
 */
object ToastUtil {
    fun show(activity: Activity, text: String) {
        if (activity.isFinishing) return
        activity.runOnUiThread(Runnable {
            // TODO Auto-generated method stub
            if (TextUtils.isEmpty(text)) return@Runnable
            Toast.makeText(activity, text, Toast.LENGTH_LONG).show()
        })
    }

    fun show(context: Context, text: String) {
        if (Constants.isMainThread())
            Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

    fun show(activity: Activity, textResId: Int) {
        if (activity.isFinishing) return
        activity.runOnUiThread {
            // TODO Auto-generated method stub
            Toast.makeText(activity, activity.getString(textResId), Toast.LENGTH_LONG).show()
        }
    }
}