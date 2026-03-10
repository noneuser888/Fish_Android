package com.wantime.wbangapp.ui.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import com.wantime.wbangapp.R
import kotlinx.android.synthetic.main.ui_dialog_message_edit.*

///创建子账户
class CreateChildDialog(context: Context) : Dialog(context, R.style.theme_dialog_alert) {

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        setContentView(R.layout.ui_create_dialog_message_edit)
        cancelText.setOnClickListener { dismiss() }
    }

    fun setTitle(title: String): CreateChildDialog {
        titleText.text = title
        return this
    }

    fun setContent(content: String): CreateChildDialog {
        descText.setText(content)
        return this
    }

    fun addConfirmListener(mListener: View.OnClickListener): CreateChildDialog {
        confirmText.setOnClickListener(mListener)
        return this
    }

    fun addCancelListener(mListener: View.OnClickListener): CreateChildDialog {
        cancelText.setOnClickListener(mListener)
        return this
    }

    fun getMessage(): String {
        return descText.text.toString()
    }
}