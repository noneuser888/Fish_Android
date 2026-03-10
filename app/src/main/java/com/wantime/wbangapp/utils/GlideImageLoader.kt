package com.wantime.wbangapp.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.wantime.wbangapp.R
import com.wantime.wbangapp.inter.ImageLoader

class GlideImageLoader  private  constructor(): ImageLoader {
    companion object{
        private var  mGlideImageLoader:GlideImageLoader?=null
        fun getInstance():GlideImageLoader{
            if(mGlideImageLoader==null){
                synchronized(GlideImageLoader::class.java){
                    mGlideImageLoader=GlideImageLoader()
                }
            }
            return mGlideImageLoader!!
        }
    }
    override fun displayImage(context: Context, path: Any, imageView: ImageView) {
        Glide.with(context).load(path).centerCrop().into(imageView)
//        Glide.with(context).load(path).placeholder(R.mipmap.ic_launcher)
//            .error(R.mipmap.ic_launcher)
//            .centerCrop().into(imageView)
    }
}