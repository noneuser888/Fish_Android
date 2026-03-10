package com.wantime.wbangapp.ui.dialog

import android.app.Dialog
import android.content.Context
import com.wantime.wbangapp.R
import kotlinx.android.synthetic.main.ui_system_ressouce_load.*


/**
 *  on 2018/1/29.
 */
class IProgressLoading : Dialog {

    constructor(context: Context) : super(context, R.style.theme_dialog_alert) {
        initView(context);
    }

    private fun initView(context: Context) {
        setContentView(R.layout.ui_system_ressouce_load)
    }

    override fun show() {
        mXLoadingView!!.start()
        super.show()
    }

    override fun dismiss() {
        mXLoadingView!!.stop()
        super.dismiss()
    }
}