package com.wantime.wbangapp.ui.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.wantime.wbangapp.utils.GlideImageLoader;

//databinding 中实现图片的加载
public class BindingAdapters {
    @BindingAdapter("android:src")
    public static void setSrc(ImageView view, int resId) {
        view.setImageResource(resId);
    }

    @BindingAdapter("imgPath") //网络图片
    public static void setSrc(ImageView view, String imgPath) {
        if (!TextUtils.isEmpty(imgPath))
            GlideImageLoader.Companion.getInstance().displayImage(view.getContext(), imgPath, view);
    }
}
