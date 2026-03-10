package com.wantime.wbangapp.ui.activity

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.webkit.*
import com.block.dog.common.util.ToastUtil
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseActivity
import kotlinx.android.synthetic.main.activity_web.*
import kotlinx.android.synthetic.main.ui_top_navigation.*
import java.lang.Exception


class IWebActivity : BaseActivity() {
    override val layoutId: Int
        get() = R.layout.activity_web

    override fun afterInitView() {
        navBack.visibility = View.VISIBLE
        navBack.setOnClickListener { finish() }
        initWebViewSetting()
        initWebViewClientSetting()
        loadContent()
    }

    private fun loadContent() {
        if (mParamJson != null) {
            val webUrl = mParamJson!!.getString("webUrl")
            navTitle.text = mParamJson?.optString("title")
            if (!openThirdApp(webUrl))
                IWebView.loadUrl(webUrl)

        }
    }

    private fun initWebViewClientSetting() {
        IWebView.webViewClient = object : WebViewClient() {
            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                super.onReceivedSslError(view, handler, error)
            }

            override fun shouldOverrideUrlLoading(view: WebView, mUrl: String): Boolean {
                if (!TextUtils.isEmpty(mUrl) && !mUrl.startsWith("http")) {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(mUrl)
                    startActivity(intent)
                    return true
                }
                return super.shouldOverrideUrlLoading(view, mUrl)
            }
        }
        IWebView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                Log.e("newProgress", ">>>>>>>>>>>>>>>>" + newProgress)
                super.onProgressChanged(view, newProgress)
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                navTitle.text = title
                super.onReceivedTitle(view, title)
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebViewSetting() {
        //声明WebSettings子类
        val webSettings: WebSettings = IWebView.settings
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true //将图片调整到适合webview的大小
        webSettings.loadWithOverviewMode = true // 缩放至屏幕的大小
        webSettings.setSupportZoom(true) //支持缩放，默认为true。是下面那个的前提。
        webSettings.builtInZoomControls = true //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.displayZoomControls = false //隐藏原生的缩放控件
        webSettings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK //关闭webview中缓存
        webSettings.allowFileAccess = true //设置可以访问文件
        webSettings.javaScriptCanOpenWindowsAutomatically = true //支持通过JS打开新窗口
        webSettings.loadsImagesAutomatically = true //支持自动加载图
        webSettings.defaultTextEncodingName = "utf-8" //设置编码格式
        webSettings.domStorageEnabled = true
        //支持https
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
    }

    /****
     * https://blog.csdn.net/gxl_1899/java/article/details/78134752
     * http://www.andnext.club/2019/01/09/WhatsNote/107-queryIntentActivities/
     * **/
    private fun openThirdApp(webUrl: String): Boolean {
        if (!webUrl.startsWith("http")) {
            try {
                if (webUrl.startsWith("sinaweibo")) {
                    val intent = Intent()
                    val cmp = ComponentName("com.sina.weibo", "com.sina.weibo.composerinde.ComposerDispatchActivity")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.component = cmp
                    intent.putExtra("uid", webUrl)
                    startActivity(intent);
                }
            } catch (ex: Exception) {
                ToastUtil.show(this, "未安装应用")
            }
            return true
        }

        return false
    }
}