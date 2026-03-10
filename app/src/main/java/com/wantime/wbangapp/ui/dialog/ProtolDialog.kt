package com.wantime.wbangapp.ui.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import com.wantime.wbangapp.R
import kotlinx.android.synthetic.main.ui_dialog_message_protol.*
import android.text.method.ScrollingMovementMethod


class ProtolDialog(context: Context) : Dialog(context, R.style.theme_dialog_alert) {

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        setContentView(R.layout.ui_dialog_message_protol)
        protolTextView.movementMethod = ScrollingMovementMethod()
        confirmText.setOnClickListener { dismiss() }
    }


    fun setContent(content: String): ProtolDialog {
        protolTextView.text = content
        return this
    }


}