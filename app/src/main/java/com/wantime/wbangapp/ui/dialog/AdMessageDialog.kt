package com.wantime.wbangapp.ui.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import com.wantime.wbangapp.R
import com.wantime.wbangapp.utils.GlideImageLoader
import kotlinx.android.synthetic.main.ui_dialog_message_ad.*

class AdMessageDialog(context: Context) : Dialog(context, R.style.theme_dialog_alert) {

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        setContentView(R.layout.ui_dialog_message_ad)
        cancelImg.setOnClickListener { dismiss() }
    }

    fun addDetailListener(mListener: View.OnClickListener): AdMessageDialog {
        dialogDetail.setOnClickListener(mListener)
        return this
    }

    fun setAdPicture(imagePath: String):AdMessageDialog {
        GlideImageLoader.getInstance().displayImage(context, imagePath, dialogDetail)
        return this
    }
}