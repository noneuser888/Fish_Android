package com.wantime.wbangapp.ui.adapter

import android.view.View

interface RecyclerAdapterItemClick {
    fun onItemClick(itemView: View, iBean: Any, position: Int)
}