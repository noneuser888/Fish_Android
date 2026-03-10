package com.wantime.wbangapp.ui.adapter

import android.view.View

interface RecyclerItemClick {
    fun onItemClick(itemView: View, iBean: Any)
}