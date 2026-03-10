package com.tencent.mm.plugin.view;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebView;

public class IWebView extends WebView {

    public IWebView(Context context) {
        this(context, null);
    }

    public IWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(getFixedContext(context), attrs, defStyleAttr);
    }

    private static Context getFixedContext(Context context) {
        // Android Lollipop 5.0 & 5.1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return context.createConfigurationContext(new Configuration());
        }
        return context;
    }
}



