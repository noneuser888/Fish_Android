package com.wantime.wbangapp.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.wantime.wbangapp.R

object ImageUtils {

    private const val localFileHead = "file://"
    private const val localFileTag = "file:"

    /**
     * 调用此方法时，一般图片View的基本属性都已经设置好了
     * **/

    fun loadImage(context: Context, imagePath: String?, imgView: ImageView) {
        if (TextUtils.isEmpty(imagePath)) return
        if (imagePath!!.startsWith(localFileTag)) {//本地图片
            loadLocalImage(context, imagePath!!, imgView)
        } else {
            val imgPaths = getImageRealPath(imagePath!!)
            Glide.with(context).load(imgPaths).into(imgView)
        }
    }

    fun loadLocalImage(context: Context, imagePath: String, imgView: ImageView) {
        if (TextUtils.isEmpty(imagePath)) return
        Glide.with(context).load(getImageLocalPath(imagePath)).error(R.mipmap.ic_launcher2).into(imgView)
    }


    fun loadImageWithDrawable(context: Context, imagePath: String, imageListener: ImageListener) {
        if (TextUtils.isEmpty(imagePath)) return
        val imgPathUrl = getImageRealPath(imagePath)
        Glide.with(context).load(imgPathUrl).into(object : SimpleTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                imageListener.onImageWithDrawable(resource)
            }
        })
    }

    private fun getImageRealPath(imagePath: String): String {
        if (imagePath.startsWith(localFileTag) || imagePath.startsWith("http") || imagePath.startsWith(
                "https"
            )
        ) return imagePath
        return Constants.baseAPIUrl + imagePath
    }

    fun getImageLocalPath(imagePath: String): String {
        if (!TextUtils.isEmpty(imagePath) && !imagePath.startsWith(localFileTag)) return localFileHead + imagePath
        return imagePath;
    }

    interface ImageListener {
        fun onImageWithDrawable(xDrawable: Drawable?)
    }
}