package com.wantime.wbangapp.ui.event

import android.view.View

interface IFormListener {
    fun onFormPost(view: View, iBean: Any)
}