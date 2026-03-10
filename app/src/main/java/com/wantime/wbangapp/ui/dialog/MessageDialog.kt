package com.wantime.wbangapp.ui.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import com.wantime.wbangapp.R
import kotlinx.android.synthetic.main.ui_dialog_message_alert.*

class MessageDialog(context: Context) : Dialog(context, R.style.theme_dialog_alert) {

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        setContentView(R.layout.ui_dialog_message_alert)
        cancelText.setOnClickListener { dismiss() }
        confirmText.setOnClickListener { dismiss() }
    }

    fun setTitle(title: String): MessageDialog {
        titleText.text = title
        return this
    }

    fun setContent(content: String): MessageDialog {
        descText.text = content
        return this
    }
    fun setConfirm(confirm:String): MessageDialog {
        confirmText.text=confirm
        return this
    }

    fun addConfirmListener(mListener: View.OnClickListener): MessageDialog {
        confirmText.setOnClickListener(mListener)
        return this
    }

    fun addCancelListener(mListener: View.OnClickListener): MessageDialog {
        cancelText.setOnClickListener(mListener)
        return this
    }


}