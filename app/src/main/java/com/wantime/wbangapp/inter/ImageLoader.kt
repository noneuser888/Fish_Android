package com.wantime.wbangapp.inter

import android.content.Context
import android.widget.ImageView

interface ImageLoader {
    fun  displayImage(context: Context, path: Any, imageView: ImageView)
}