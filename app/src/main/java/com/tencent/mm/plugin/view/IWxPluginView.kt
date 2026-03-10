package com.tencent.mm.plugin.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.lifecycle.LifecycleOwner
import com.block.dog.common.util.ToastUtil
import com.tencent.mm.plugin.base.utils.CalculateByte
import com.wantime.wbangapp.R
import com.wantime.wbangapp.model.PostBackBean
import com.wantime.wbangapp.model.UpdateRecordBean
import com.wantime.wbangapp.request.IWebSocketClient
import com.wantime.wbangapp.request.ValueCallBack
import com.wantime.wbangapp.ui.activity.IPlatformApplyActivity
import com.wantime.wbangapp.ui.activity.SingleRecordActivity
import com.wantime.wbangapp.ui.dialog.IProgressLoading
import com.wantime.wbangapp.ui.dialog.MessageDialog
import com.wantime.wbangapp.ui.event.MessageEvent
import com.wantime.wbangapp.utils.Constants
import com.wantime.wbangapp.utils.JsScriptUtils
import com.wantime.wbangapp.viewmodel.WXAuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import org.jsoup.Jsoup
import java.net.URI
import java.util.*

class IWxPluginView constructor(
    private val mActivity: Activity,
    private val application: Application,
    private val intent: Intent?,
    private var mParamJson: JSONObject?,
    private val wxWebview: WebView,
    private val showQrText: TextView,
    private val fusText: TextView,
    private val wxKeyText: TextView,
    private val wxKeyText1:TextView,
    private val radioButton1: RadioButton,
    private val radioButton2: RadioButton,
    private val radioButton3: RadioButton,
    private val iWebCover: View,
    private val iQrImageView: ImageView,
    private val uiWxOnLine: TextView,
    private val mActionBar: ActionBar,
    private val mLifeOwner: LifecycleOwner
) {
    private var redirectUrl: String? = null
    private var appId: String? = null
    private var authOrderId: String? = null
    private var mIntent: Intent? = null

    //传值
    private var appPackage: String? = null
    private var message_token: String? = null
    private var wxapi_basereq_transaction: String? = null
    private var wxapi_command_type: Int = 1
    private var mmessage_content: String? = null
    private var wxapi_basereq_openid: String? = null
    private var wxapi_sendauth_req_scope: String? = null
    private var wxapi_sendauth_req_state: String = ""
    private var wxapi_baseresp_errstr: String? = null
    private var wxapi_app_nick_name: String? = null
    private var wxapi_app_icon_url: String? = null

    private var mViewModel: WXAuthViewModel? = null
    private var isShowQrCode = false //是否显示二维码
    private var qrTempFileName = ""
    private var qrLink = ""
    private var zsAuthType = -1 //0是显示二维码、1是专属授权 2是一键授权
    private var mProgressDialog: IProgressLoading? = null
    private val SDK_INT = 621086464

    //弹出框
    private var xMessageDialog: MessageDialog? = null

    //二维码的值
    private var mQrResultData: String = ""

    //holder 界面要多少
    private val holderProgressTime = 5 * 60 * 1000L

    //复扫的订单ID
    private var RepeatOrderId = ""

    //websocket
    private var mWebSocket: IWebSocketClient? = null

    //uuid
    private var mUUIDString: String = ""

    //zs对戒状态
    private var zsStatus = -1

    private var xTimer: Timer? = null

    //用户没有实名认证则提示
    private var messageDialog: MessageDialog? = null
    private var  oneKeyType=-1 //一键授权类型
    private var  grade = 1
    private var mFirmQrPath="https://open.weixin.qq.com/connect/confirm?uuid={0}"
    ///二维码识别出来的内容,现在改成自己生成
    private var mQrCodeWebContent=""
    ///微信要重新调一下
    private var mAutoRedirectUrl="https://long.open.weixin.qq.com/connect/l/qrconnect?uuid={UUID}&f=url&_={Time}"

    ///需要特殊处理的包名 //com.to.mhtgd
    private var mNoOpenWxPackArray= arrayOf("")
    ///不需要启动主包的APP
    private var mNoLauncherArray= arrayOf("com.tencent.tmgp.wbbuyu","com.qqlgame.fish.qql","com.duoduo.tuanzhang")
    private var isJumpApp=false


    @SuppressLint("HandlerLeak")
    private val mHandler = object : Handler() {

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                Mssage_Hidden -> {
                    mProgressDialog?.setCancelable(true)
                    mProgressDialog?.dismiss()
                }

                Massge_Fixed_show -> {
                    mProgressDialog?.setCancelable(false)
                    mProgressDialog?.show()
                    startTimer()
                }

                Massge_show_Alert -> {
                    val message = msg.obj as PostBackBean
                    xMessageDialog?.setTitle("系统提示")!!.setContent(message.message)
                        .addConfirmListener(View.OnClickListener {

                            xMessageDialog?.dismiss()
                        })
                        .show()
                }
                Massge_hidden_Qr -> {
                    isShowQrCode = true
                    iWebCover.visibility = View.GONE
                }
                Message_Apply_PlatForm -> {
                    xMessageDialog!!.setTitle(mActivity.getString(R.string.ui_system_title))
                        .addConfirmListener(View.OnClickListener {
                            xMessageDialog?.dismiss()
                            applyPlatForm()
                            mActivity.finish()
                        })
                        .setContent(mActivity.getString(R.string.ui_platform_no_exist)).show()
                }
            }
        }
    }

    private val Mssage_Hidden = 0
    private val Massge_Fixed_show = 1
    private val Massge_show_Alert = 2
    private val Massge_hidden_Qr = 3
    private val Message_Apply_PlatForm = 4


    fun initView() {
        Constants.createTempFiles()
        initProgress()
        initWebViewSetting()
        initWebView()
        initViewAndEvent()
        initLoadData()
        initLoad()
        onInitCheckDialog()
        onSettingWebCoverPosition()
    }


    private fun initWebView() {
        Log.e("mParamJson", mParamJson.toString())
        xMessageDialog = MessageDialog(mActivity)
        mViewModel = WXAuthViewModel(application)
        onWebViewClean()
        wxWebview.webViewClient = IWebViewClient()
        wxWebview.webChromeClient = IWebChromeClient()
        wxWebview.addJavascriptInterface(IJavascriptInterface(), "handler")
    }

    private fun initLoad() {
        Log.e("plugin_qrlink", qrLink)
        wxWebview.loadUrl(qrLink)
    }


    private fun initViewAndEvent() {
//        navRightIcon.text = "复扫"
//        navRightIcon1.text = "刷新"
//        navRightIcon.visibility = View.VISIBLE
//        navRightIcon1.visibility = View.VISIBLE
//        Constants.onSetDrawableLeft(mActivity, navRightIcon1, R.mipmap.shuaxin)
        fusText.setOnClickListener {
            if (Constants.isLogin(mActivity)) {
                openRecord()
            }
        }//打开复扫记录

        showQrText.setOnClickListener {
            zsAuthType = 0;if (Constants.isLogin(mActivity)) {
            showQrDiv()
        }
        }
       // wxZsText.setOnClickListener {
       //     zsAuthType = 1; if (Constants.isLogin(mActivity)) {
       //     onExclusiveAuth()
       // }
      //  }
        wxKeyText.setOnClickListener {
            zsAuthType = 2
            oneKeyType=1
            if (Constants.isLogin(mActivity)) {
                updateGrade()
                onKeyAuthority()
            }
        }
        wxKeyText1.setOnClickListener {
            zsAuthType = 2
            oneKeyType=2
            if (Constants.isLogin(mActivity)) {
                updateGrade()
                onKeyAuthority()
            }
        }
    }
    private  fun updateGrade(){
        if(radioButton2.isChecked){
            grade=2;
        }else if(radioButton3.isChecked){
            grade=3;
        }else{
            grade=1;
        }
    }
    private fun onInitCheckDialog() {
        messageDialog = MessageDialog(mActivity)
            .setTitle(mActivity.getString(R.string.ui_system_title))
            .setContent(mActivity.getString(R.string.ui_not_real_name_tips))
            .setConfirm("去认证")
            .addConfirmListener(View.OnClickListener {
                messageDialog?.dismiss()
                Constants.onGotoIdCardVerif(mActivity)
            })
        messageDialog!!.setCancelable(false)
    }

    private fun onWebViewClean() {
        wxWebview.clearCache(true)
        wxWebview.clearHistory()
        wxWebview.clearFormData()
    }

    private fun parseBundle() {
        if (intent != null) {
            val bundle = intent.extras
            if (bundle != null) {
                appPackage = bundle.getString("_mmessage_appPackage", "")
                val message_content = bundle.getString("_mmessage_content", "")
                val messages = message_content!!.split("appid=")
                appId = messages[1]
                message_token = bundle.getString("_message_token", "");
                wxapi_command_type = bundle.getInt("_wxapi_command_type", 0);
                wxapi_basereq_openid = bundle.getString("_wxapi_basereq_openid", "");
                wxapi_sendauth_req_scope = bundle.getString("_wxapi_sendauth_req_scope", "");
                wxapi_sendauth_req_state = bundle.getString("_wxapi_sendauth_req_state", "");
                wxapi_basereq_transaction = bundle.getString("_wxapi_basereq_transaction", "");
                wxapi_basereq_openid = bundle.getString("_wxapi_basereq_openid", "");
                qrLink = Constants.onGetWxOpenLink(
                    appId!!,
                    appPackage!!,
                    wxapi_sendauth_req_scope!!,
                    wxapi_sendauth_req_state,
                    2
                )
                Log.e("dfa",""+qrLink)
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebViewSetting() {
        //声明WebSettings子类
        val webSettings: WebSettings = wxWebview.settings
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

        //设置UA
        val ua: String = webSettings.userAgentString
        val uaStr = StringBuilder(ua)
        uaStr.append(" MicroMessenger/7.0.0(0x17000024) NetType/WIFI Language/zh_CN")
        webSettings.userAgentString = uaStr.toString()
        //支持https
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
    }

    private inner class IWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, webUrl: String?): Boolean {
            Log.e("webUrl", ">>>>>>>>>>>>>>>>$webUrl")
            if (!TextUtils.isEmpty(webUrl) && webUrl!!.contains("://oauth")) {
                showProgress()
                redirectUrl =Constants.onURLDecoder( webUrl)
                iWebCover.post {
                    iWebCover.visibility = View.GONE
                    iQrImageView.visibility=View.GONE
                }
                view?.loadUrl(JsScriptUtils.showBodyJs)
                return true
            }
            return super.shouldOverrideUrlLoading(view, webUrl)
        }

        override fun onPageFinished(view: WebView, url: String?) {
            super.onPageFinished(view, url)
            Log.e("onPageFinished", ">>>>>>>>>>>>>>>>>>>onPageFinished")
            if (!TextUtils.isEmpty(url) && Constants.isHttpLink(url!!)) {
//                view.loadUrl(
//                    """javascript:(function() {function getOffsetTop(el){return el.offsetParent ? el.offsetTop + getOffsetTop(el.offsetParent): el.offsetTop};
//                        |function getOffsetLeft(el){return el.offsetParent? el.offsetLeft + getOffsetLeft(el.offsetParent): el.offsetLeft};
//                        |var authItem=document.getElementsByClassName("auth_qrcode")[0];  var imgSrc=authItem.getAttribute('src');
//                        |authItem.setAttribute('src',imgSrc); authItem.addEventListener('error',function(e){window.handler.imageLoadFailed();});
//                        |var oLeft=getOffsetLeft(authItem);var oTop=getOffsetTop(authItem);var iHeight=authItem.height;var iWidth=authItem.width;
//                        |window.handler.getContent(document.body.innerHTML);window.handler.imageLayoutPosition(oLeft,oTop,iWidth,iHeight);})()""".trimMargin()
//                )
                view.loadUrl(
                    """javascript:(function() {function getOffsetTop(el){return el.offsetParent ? el.offsetTop + getOffsetTop(el.offsetParent): el.offsetTop};
                        |function getOffsetLeft(el){return el.offsetParent? el.offsetLeft + getOffsetLeft(el.offsetParent): el.offsetLeft};
                        |var authItem=document.getElementsByClassName("auth_qrcode")[0];  var imgSrc=authItem.getAttribute('src');
                        |authItem.setAttribute('src',imgSrc);  authItem.addEventListener('error',function(e){window.handler.imageLoadFailed();});
                        |authItem.setAttribute('src',imgSrc);
                        |var oLeft=getOffsetLeft(authItem);var oTop=getOffsetTop(authItem);var iHeight=authItem.height;var iWidth=authItem.width;
                        |window.handler.getContent(document.body.innerHTML);window.handler.imageLayoutPosition(oLeft,oTop,iWidth,iHeight);})()""".trimMargin()
                )


            }

        }

    }


    private inner class IWebChromeClient : WebChromeClient() {}

    private inner class IJavascriptInterface {
        @JavascriptInterface
        fun getContent(Content: String) {
            if (TextUtils.isEmpty(mUUIDString))
                GlobalScope.launch(Dispatchers.IO) {
                    //                    Log.e("Content", Content)
                    try {
                        val htmlDoc = Jsoup.parse(Content)
                        val nameItem = htmlDoc.getElementsByTag("strong")[0]
                        val jsItem = htmlDoc.getElementsByTag("script")[0]
                        val iconItem = htmlDoc.getElementsByClass("auth_avatar")[0]
                        val mjsText = jsItem.html().replace("\n", "")
                            .replace(" ", "")
                            .replace(":", "")
                            .replace(",", "")
                            .replace("\"", "")
                        val uIndex = mjsText.indexOf("uuid")
                        val endIndex = mjsText.indexOf("appid")
                        mUUIDString = mjsText.substring(uIndex + 4, endIndex)
                        wxapi_app_nick_name = nameItem.text()
                        wxapi_app_icon_url = iconItem.attr("src")
                        mQrCodeWebContent=mFirmQrPath.replace("{0}",mUUIDString)
                        GlobalScope.launch(Dispatchers.Main) { Constants.OnBirthQrImage(iQrImageView,mQrCodeWebContent) }

                        var order = mParamJson!!.optString("order")
                        if(!order.isEmpty()){
                            mParamJson!!.put("order",null)
                            val messageEvent = MessageEvent()
                            messageEvent.mType = MessageEvent.EventType.reaptScan.ordinal
                            messageEvent.dataJson = order
                            EventBus.getDefault().post(messageEvent)
                        }
                    } catch (ex: Exception) {
                    }
                }
        }

        @JavascriptInterface
        fun parse(HContent: String) {
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    if (redirectUrl!!.contains("://oauth") && redirectUrl!!.contains("code")) {
                        val xContent = Jsoup.parse(HContent)
                        val hItem = xContent.getElementsByClass("weui-msg__title")[0]
                        val innerText = hItem.text()
                        val nText = innerText.substring(0, innerText.indexOf("授权"))
                        onQRAuthSuccess(nText)
                    }
                } catch (ex: Exception) {
//                    hiddenProgress()
                }
            }
        }

        @JavascriptInterface
        fun imageLoadFailed() {
//            wxWebview.postDelayed({
//                onWebViewClean()
//                wxWebview.reload()
//            }, 200)
//            Log.e("图片加载失败了", ">>>>>>>>>>>>>>>>>>>>>>失败了！")
        }

        @JavascriptInterface
        fun imageLayoutPosition(left: Int, top: Int, width: Int, height: Int) {
            if (!isShowQrCode)
                iWebCover.post {
                    Log.e("imageLayoutPosition", ">>>>>>>>>>>>>>>>$left,$top,$width,$height")
                    Log.e("imageLayoutPosition", ">>>>>>>>>>>>>>>>$left,$top,$width,$height")
                    val iWebCoverLayout = iWebCover.layoutParams as FrameLayout.LayoutParams
                    iWebCoverLayout.topMargin = Constants.dip2px(iWebCover.context, top.toFloat())
                    iWebCoverLayout.leftMargin = Constants.dip2px(iWebCover.context, left.toFloat())
                    iWebCoverLayout.width = Constants.dip2px(iWebCover.context, width.toFloat())
                    iWebCoverLayout.height = Constants.dip2px(iWebCover.context, height.toFloat())
                    iWebCover.layoutParams = iWebCoverLayout
                    iWebCover.visibility = View.VISIBLE
                    OnPositionView(iWebCover,left,top,width,height)
                    OnPositionView(iQrImageView,left,top,width,height)


                    val xPosition=Constants.onGetWebViewPosition()
                    if(xPosition.left<1&&xPosition.width<1){
                        val positionJson=JSONObject()
                        positionJson.put("top",top)
                        positionJson.put("left",left)
                        positionJson.put("width",width)
                        positionJson.put("height",height)
                        Constants.onSaveWebViewPosition(mActivity,positionJson)
                    }
                }
        }
    }

    //重新定位View
    private fun OnPositionView(iWebCover :View ,left: Int, top: Int, width: Int, height: Int){
        val iWebCoverLayout = iWebCover.layoutParams as FrameLayout.LayoutParams
        iWebCoverLayout.topMargin = Constants.dip2px(iWebCover.context, top.toFloat())
        iWebCoverLayout.leftMargin = Constants.dip2px(iWebCover.context, left.toFloat())
        iWebCoverLayout.width = Constants.dip2px(iWebCover.context, width.toFloat())
        iWebCoverLayout.height = Constants.dip2px(iWebCover.context, height.toFloat())
        iWebCover.layoutParams = iWebCoverLayout
        iWebCover.visibility = View.VISIBLE
    }

    fun onMessageEvent(event: MessageEvent) {
        when (event.mType) {
            MessageEvent.EventType.reaptScan.ordinal -> {
                try {
                    val mResult = JSONObject(event.dataJson)
                    RepeatOrderId = mResult.optString("id")
                    val tempText = mActionBar.title!!
                    if (!tempText.contains("复扫")) mActionBar.title = "$tempText（复扫）"

                    zsAuthType = 2
                    oneKeyType=1
                    //grade=1
                    if (Constants.isLogin(mActivity)) {
                        updateGrade()
                        onKeyAuthority()
                    }
                } catch (ex: java.lang.Exception) {
                }
            }

            MessageEvent.EventType.socket.ordinal -> {
                try {
                    val message = JSONObject(event.dataJson)
                    zsStatus = message.optString("status", "-1").toInt()
                } catch (ex: Exception) {
                }
            }
            MessageEvent.EventType.appParam.ordinal -> {
                mParamJson = JSONObject(event.dataJson)
                initLoadData()
                onWebViewClean()
                wxWebview.loadUrl(qrLink)
                Log.e("plugin", ">>>>>>>>>>>>>>>>>>>>>>>>>$mParamJson")
            }
        }
    }

    private fun showQrDiv() {
        onQRCodeAuth()
    }

    //截图QR
    private fun onScreenShot(valueCallBack: ValueCallBack) {
        GlobalScope.launch {
            wxWebview.isDrawingCacheEnabled = true
            wxWebview.buildDrawingCache()
            val bitmap: Bitmap = wxWebview.drawingCache
            val mLayoutParam = iWebCover.layoutParams as FrameLayout.LayoutParams
            val qrBitmap = Bitmap.createBitmap(
                bitmap,
                mLayoutParam.leftMargin,
                mLayoutParam.topMargin,
                mLayoutParam.width,
                mLayoutParam.height
            )
            wxWebview.isDrawingCacheEnabled = false
            GlobalScope.launch((Dispatchers.Main)) {
                Constants.onGetQrInfo(qrBitmap, object : ValueCallBack {
                    override fun onValueBack(mResultAny: Any) {
                        qrBitmap.recycle()
                        bitmap.recycle()
                        valueCallBack.onValueBack(mResultAny)
                    }
                })
            }
        }
    }

    //一键授权 old
//    private fun onKeyAuthority() {
//        onScreenShot(object : ValueCallBack {
//            override fun onValueBack(mResultAny: Any) {
//                if (!TextUtils.isEmpty(appId) && !TextUtils.isEmpty(mResultAny.toString())) {
//                    onKeyAuthorityRequest(mResultAny.toString(), appId!!, wxapi_sendauth_req_state)
//                }
//                if (TextUtils.isEmpty(mResultAny.toString()))
//                    ToastUtil.show(mActivity, mActivity.getString(R.string.ui_qrcode_error))
//            }
//        })
//    }

    //一键授权 新修改,二维码直接计算出来
    private fun onKeyAuthority() {
        if (!TextUtils.isEmpty(appId) && !TextUtils.isEmpty(mQrCodeWebContent)) {
            onKeyAuthorityRequest(mQrCodeWebContent, appId!!, wxapi_sendauth_req_state)
        }
    }


    //专属授权
//    private fun onExclusiveAuth() {
//        showZSLayout(true)
//        onScreenShot(object : ValueCallBack {
//            override fun onValueBack(mResultAny: Any) {
//                if (!TextUtils.isEmpty(appId) && !TextUtils.isEmpty(mResultAny.toString())) {
//                    onExclusiveAuthRequest(mResultAny.toString(), appId!!, mUUIDString)
//                }
//                if (TextUtils.isEmpty(mResultAny.toString()))
//                    ToastUtil.show(mActivity, mActivity.getString(R.string.ui_qrcode_error))
//            }
//        })
//    }


    private fun initLoadData() {
        if (mParamJson != null && mParamJson!!.length() > 0) {
            Log.e("mParamJson2", mParamJson.toString())
            var price = mParamJson!!.optString("price1", "")
            radioButton1.text="日("+price+")"
            price = mParamJson!!.optString("price2", "")
            radioButton2.text="周("+price+")"
            price = mParamJson!!.optString("price3", "")
            radioButton3.text="月("+price+")"

            appId = mParamJson!!.optString("appId", "")
            val scope = mParamJson!!.optString("scope", "")
            appPackage = mParamJson!!.optString("bundleId", "")
            wxapi_sendauth_req_state = Constants.onGetState(mParamJson!!.optString("scope", ""))
            qrLink =
                Constants.onGetWxOpenLink(appId!!, mParamJson!!.optString("bundleId"), scope, "", 1)
        } else {
            parseBundle()
        }
        mViewModel?.onGetOnlineNumber(appId!!)?.observe(mLifeOwner,
            androidx.lifecycle.Observer {
                uiWxOnLine.post {
                    uiWxOnLine.text =
                        mActivity.getString(R.string.ui_online_number_1) + it.code
                }
            })
    }

    fun openRecord() {
        if (mParamJson != null) {
            if (mParamJson != null && mParamJson!!.length() < 1) {
                mParamJson = JSONObject()
                mParamJson!!.put("appId", appId)
                mParamJson!!.put("nickname", wxapi_app_nick_name)
            }
            val mIntent = Intent(mActivity, SingleRecordActivity::class.java)
            mIntent.putExtra("data", mParamJson.toString())
            mActivity.startActivity(mIntent)
        }
    }

    fun onRefresh() {
        onWebViewClean()
        wxWebview.reload()
    }


    private fun initProgress() {
        mProgressDialog = IProgressLoading(mActivity)
    }

    fun showProgress() {
        if (!mActivity.isFinishing)
            mProgressDialog?.run { mProgressDialog?.show() }
    }

    fun hiddenProgress() {
        Log.e("hiddenProgress", ">>>>>>>>>>>>>>>>>>>>>>>hiddenProgress")
        if (!mActivity.isFinishing)
            mProgressDialog?.run { mProgressDialog?.dismiss() }
    }

    private fun hiddenProgress(fixed: Boolean) {
        Log.e("hiddenProgress", ">>>>>>>$Mssage_Hidden")
        mHandler.sendEmptyMessage(Mssage_Hidden)
    }

    //固定界面
    private fun showProgress(fixed: Boolean) {
        redirectUrl = null
        mHandler.sendEmptyMessage(Massge_Fixed_show)
    }

    private fun startTimer() {
        if (xTimer == null) {
            xTimer = Timer()
            xTimer!!.schedule(object : TimerTask() {
                override fun run() {
                    Log.e("redirectUrl", "timer>>>>>>>>$redirectUrl")
                    if (TextUtils.isEmpty(redirectUrl)) {
                        mHandler.sendEmptyMessage(Mssage_Hidden)
                    }
                    stopTimer()
                }
            }, holderProgressTime, 1000)
        }
    }

    fun stopTimer() {
        if (xTimer != null) {
            xTimer!!.cancel()
        }
        xTimer = null
    }

    //二维码扣费成功后调用
    private fun onQRCodeAuth() {
        mViewModel?.onQRCodeAuth()?.observe(mLifeOwner, androidx.lifecycle.Observer {
            authOrderId = it.authOrderId
            onUpdateRecord()
            if (TextUtils.isEmpty(authOrderId)) {
                if (it.ok == Constants.NET_REAL_NAME) {
                    messageDialog?.show()
                } else {
                    val message = Message()
                    message.what = Massge_show_Alert
                    message.obj = it
                    mHandler.sendMessage(message)
                }
            } else mHandler.sendEmptyMessage(Massge_hidden_Qr)
        })
    }

    //二维码成功跳转后调用
    private fun onQRAuthSuccess(name: String) {
        if (!TextUtils.isEmpty(authOrderId)) {
            mViewModel?.onQrCodeSuccess(authOrderId!!, redirectUrl!!, name)
                ?.observe(mLifeOwner,
                    androidx.lifecycle.Observer {
                        onUpdateRecord()
                        closeAndBack()
                    })
        } else {
            //ToastUtil.show(mActivity, mActivity.getString(R.string.ui_param_error))
            //hiddenProgress()
            closeAndBack()
        }
    }

    //更新授权记录
    private fun onUpdateRecord() {
        val postBean = UpdateRecordBean()
        postBean.authRecordId = if (TextUtils.isEmpty(authOrderId)) "" else authOrderId!!
        postBean.appId = if (TextUtils.isEmpty(appId)) "" else appId!!
        postBean.imageUrl = if (TextUtils.isEmpty(wxapi_app_icon_url)) "" else wxapi_app_icon_url!!
        postBean.platformName =
            if (TextUtils.isEmpty(wxapi_app_nick_name)) "" else wxapi_app_nick_name!!
        mViewModel?.updateRecord(postBean)?.observe(mLifeOwner, androidx.lifecycle.Observer {
            Log.e("onUpdateRecord", "" + it.message)
        })
    }

    //一键授权接口请求
    private fun onKeyAuthorityRequest(qrCode: String, appId: String, state: String) {
        showProgress(true)
        GlobalScope.launch(Dispatchers.Main) {
            mViewModel?.onKeyAuthorityRequest(
                qrCode, appId, if (TextUtils.isEmpty(RepeatOrderId)) "" else RepeatOrderId
                , state,oneKeyType,1,grade
            )?.observe(mLifeOwner, androidx.lifecycle.Observer {
                when (it.ok) {
                    Constants.NET_OK -> {
                        authOrderId = it.authOrderId//模态框暂时不隐藏
                        if (!TextUtils.isEmpty(it.redirect_url)) {//已经返回跳转的连接地址了那么直接跳转
                            redirectUrl = it.redirect_url
                            iWebCover.post {
                                iWebCover.visibility = View.GONE
                                iQrImageView.visibility=View.GONE
                            }
                            closeAndBack()
                        }else onAutoQueryWXAuth()
                    }
                    Constants.NET_APPLY_PLATFORM -> {
                        mHandler.sendEmptyMessage(Message_Apply_PlatForm)
                        hiddenProgress(true)
                    }
                    Constants.NET_REAL_NAME -> {
                        messageDialog?.show()
                        hiddenProgress(true)
                    }
                    else -> {
                        ToastUtil.show(mActivity, it.message)
                        hiddenProgress(true)
                    }
                }
            })

        }
    }
    //自动调用微信回调,抓取重定向地址
    private fun onAutoQueryWXAuth(){
        if(!TextUtils.isEmpty(mUUIDString)){
            val tempUrl=mAutoRedirectUrl.replace("{UUID}",mUUIDString).replace("{Time}",System.currentTimeMillis().toString())
            GlobalScope.launch(Dispatchers.Main) {
                mViewModel?.OnRequestWXAuth(tempUrl)
                    ?.observe(mLifeOwner, androidx.lifecycle.Observer {
                        GlobalScope.launch {
                            if (it.ok == Constants.NET_OK) {
                                redirectUrl = it.redirect_url
                                closeAndBack()
                            } else {
                                delay(5 * 1000)
                                onAutoQueryWXAuth()
                            }
                        }

                    })
            }
        }
    }

//    //专属授权接口请求
//    private fun onExclusiveAuthRequest(qrCode: String, appId: String, uuid: String) {
//        GlobalScope.launch(Dispatchers.Main) {
//            mViewModel?.onExclusiveAuthRequest(qrCode, appId, uuid)
//                ?.observe(mLifeOwner, androidx.lifecycle.Observer {
//                    when (it.ok) {
//                        Constants.NET_OK -> {
//                            authOrderId = it.authOrderId
//                            initWebSocket()
//                        }
//                        Constants.NET_APPLY_PLATFORM -> mHandler.sendEmptyMessage(
//                            Message_Apply_PlatForm
//                        )
//                        else -> ToastUtil.show(mActivity, it.message)
//                    }
//                    hiddenProgress(true)
//                })
//        }
//    }

    //初始化socket
    private fun initWebSocket() {
        GlobalScope.launch(Dispatchers.IO) {
            val userId = Constants.onGetUserBaseInfoWithToken().id
            if (mWebSocket == null || mWebSocket!!.isClosed) {
                val address = Constants.webSocket.replace("{userId}", userId)
                val mURI = URI(address)
                mWebSocket = IWebSocketClient(mURI)
                mWebSocket?.connect()
            }
        }
    }

    //操作成功后返回页面
    private fun closeAndBack() {
       if(!mActivity.isFinishing&&!isJumpApp){
           isJumpApp=true
           mActivity.runOnUiThread {
               if (!TextUtils.isEmpty(appPackage) && !TextUtils.isEmpty(redirectUrl)) {
                   try {
//                       val startIndex=redirectUrl!!.indexOf("&lang")
//                       val endIndex=redirectUrl!!.indexOf("&state")
//                       val trimChar=redirectUrl!!.substring(startIndex,endIndex)
//                       redirectUrl=redirectUrl!!.replace(trimChar,"")

                       val params = redirectUrl!!.split("code=").toTypedArray()
                       val params2 = params[1].split("&").toTypedArray()
                       val code = params2[0]
//                       val params3 = params2[1].split("state=").toTypedArray()
//                       val state: String = if (params3.size >= 2) params3[1] else ""
                       val state: String =onGetWXSate(redirectUrl!!)
                       mActivity.startActivity(CalculateByte.auth(redirectUrl,code,appPackage,state))


                       /*原浪潮跳转代码
                       val intent = Intent()
                       intent.setClassName(appPackage!!, "$appPackage.wxapi.WXEntryActivity")
                       val bundle = Bundle()
                       bundle.putString("_wxapi_sendauth_resp_state", state)
                       bundle.putString("_wxapi_sendauth_resp_token", code)
                       bundle.putString("_wxapi_baseresp_transaction", wxapi_basereq_transaction)
                       bundle.putString("_wxapi_sendauth_resp_lang", "zh_CN")
                       bundle.putInt("_wxapi_command_type", wxapi_command_type)
                       bundle.putString("_wxapi_sendauth_resp_country", "CN")
                       bundle.putString("wx_token_key", "com.tencent.mm.openapi.token")
                       bundle.putString("_wxapi_sendauth_resp_url", redirectUrl)
                       bundle.putInt("_wxapi_baseresp_errcode", 0)
                       bundle.putString("_wxapi_baseresp_errstr", wxapi_baseresp_errstr)
                       bundle.putString("_wxapi_baseresp_openId", wxapi_basereq_openid)
                       intent.putExtras(bundle)
                       intent.putExtra("_wxapi_sendauth_resp_auth_result", true)
                       intent.putExtra("_mmessage_sdkVersion", SDK_INT)
                       intent.putExtra("_mmessage_appPackage", "com.tencent.mm")
                       intent.putExtra("_mmessage_content", mmessage_content)
                       val checksum = CalculateByte.getBytes(
                           SDK_INT,
                           "com.tencent.mm"
                       )
                       intent.putExtra("_mmessage_checksum", checksum)

                       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                           .addFlags(Intent.FLAG_RECEIVER_NO_ABORT)
                       when {
                           mNoLauncherArray.contains(appPackage) -> {
                               mActivity.startActivity(intent)
                           }
                           else -> {
                               startMain(appPackage!!)
                               Thread.sleep(1000)
                               mActivity.startActivity(intent)
                           }
                       }

                       *//*
                       大象平台com.cm.elephant跳转代码
                       com.cm.tiger.model.App.auth()

                       Intent intent = new Intent();
                       String pkg = pkg();
                       intent.setComponent(new ComponentName(pkg, pkg() + ".wxapi.WXEntryActivity"));
                       intent.setFlags(268435456);
                       intent.putExtra("_mmessage_appPackage", "com.tencent.mm");
                       String str3 = null;
                       intent.putExtra("_mmessage_content", str3);
                       intent.putExtra("_mmessage_sdkVersion", 637928960);
                       intent.putExtra("_mmessage_checksum", checkSum(intent.getStringExtra("_mmessage_content"), intent.getIntExtra("_mmessage_sdkVersion", 0), intent.getStringExtra("_mmessage_appPackage")));
                       intent.putExtra("_message_token", str3);
                       intent.putExtra("wx_token_key", "com.tencent.mm.openapi.token");
                       intent.putExtra("_wxapi_sendauth_resp_state", state());
                       intent.putExtra("_wxapi_sendauth_resp_token", str2);
                       intent.putExtra("_wxapi_baseresp_transaction", str3);
                       intent.putExtra("_wxapi_sendauth_resp_lang", "zh_CN");
                       intent.putExtra("_wxapi_command_type", 1);
                       intent.putExtra("_wxapi_sendauth_resp_country", "CN");
                       intent.putExtra("_wxapi_sendauth_resp_auth_result", false);
                       intent.putExtra("_wxapi_sendauth_resp_url", str);
                       intent.putExtra("_wxapi_baseresp_errcode", 0);
                       intent.putExtra("_wxapi_baseresp_errstr", str3);
                       intent.putExtra("_wxapi_baseresp_openId", str3);


                       *//*
                       红豆平台cn.erkaisi.hongdou跳转代码
                       com.wx.games.Utils.AuthUtils.auth()


                       if (state == null && state.length() == 0) {
                           state = "1606139357678";
                       }
                       Intent intent = new Intent();
                       intent.setComponent(new ComponentName(appPackage, appPackage + ".wxapi.WXEntryActivity"));
                       intent.putExtra("_mmessage_appPackage", "com.tencent.mm");
                       String content = null;
                       intent.putExtra("_mmessage_content", content);
                       intent.putExtra("_mmessage_sdkVersion", 637928960);
                       intent.putExtra("_mmessage_checksum", checkSum(intent.getStringExtra("_mmessage_content"), intent.getIntExtra("_mmessage_sdkVersion", 0), intent.getStringExtra("_mmessage_appPackage")));
                       intent.putExtra("_message_token", content);
                       intent.putExtra("wx_token_key", "com.tencent.mm.openapi.token");
                       intent.putExtra("_wxapi_sendauth_resp_state", state);
                       intent.putExtra("_wxapi_sendauth_resp_token", token);
                       intent.putExtra("_wxapi_baseresp_transaction", content);
                       intent.putExtra("_wxapi_sendauth_resp_lang", "zh_CN");
                       intent.putExtra("_wxapi_command_type", 1);
                       intent.putExtra("_wxapi_sendauth_resp_country", "CN");
                       intent.putExtra("_wxapi_sendauth_resp_auth_result", false);
                       intent.putExtra("_wxapi_sendauth_resp_url", redirectUrl);
                       intent.putExtra("_wxapi_baseresp_errcode", 0);
                       intent.putExtra("_wxapi_baseresp_errstr", content);
                       intent.putExtra("_wxapi_baseresp_openId", content);
                       */

                   } catch (ex: Exception) {
                       Log.e("mActivity",ex.message)
                   }
                   Thread.sleep(50)
                   mActivity.finish()
               }
           }
       }
    }

    private fun startMain(packageName: String) {
        try {
            val minIntent = mActivity.packageManager.getLaunchIntentForPackage(packageName)
            minIntent?.setPackage(null) // 加上这句代码
            mActivity.startActivity(minIntent)
        } catch (ex: java.lang.Exception) {
            Log.e("lauch", "$packageName>>>>>>>>>>>>>>>>>>>>$ex")
        }
    }


    private fun onGetWXSate(originalWxCode:String ):String{
           if(originalWxCode.isNotEmpty()){
               val tempArray=originalWxCode.split("&")
               for (itemData in tempArray){
                   if(itemData.toLowerCase().startsWith("state=")){
                       return itemData.substring(6)
                   }
               }
           }
        return ""
    }


//    private fun showZSLayout(isSHow: Boolean) {
//        when (isSHow) {
//            true -> zsLayout.post {
//                zsLayout.visibility = View.VISIBLE
//                mXLoadingView.setNormalColor("#888888")
//                mXLoadingView.start()
//            }
//            else -> zsLayout.post {
//                zsLayout.visibility = View.GONE
//                mXLoadingView.stop()
//            }
//        }
//    }

    //申请新平台
    private fun applyPlatForm() {
        val mIntent = Intent(mActivity, IPlatformApplyActivity::class.java)
        val dataJson = JSONObject()
        dataJson.put("_mmessage_appPackage", appPackage)
        dataJson.put("appid", appId)
        dataJson.put("_message_token", message_token)
        dataJson.put("_wxapi_command_type", wxapi_command_type)
        dataJson.put("_wxapi_basereq_openid", wxapi_basereq_openid)
        val tempScope = "scope=$wxapi_sendauth_req_scope&state=$wxapi_sendauth_req_state"
        dataJson.put("_wxapi_sendauth_req_scope", tempScope)
        dataJson.put("_wxapi_sendauth_req_state", wxapi_sendauth_req_state)
        dataJson.put("_wxapi_basereq_transaction", wxapi_basereq_transaction)
        dataJson.put("_wxapi_basereq_openid", wxapi_basereq_openid)
        dataJson.put("_wxapi_app_nick_name", wxapi_app_nick_name)
        dataJson.put("_wxapi_app_icon_url", wxapi_app_icon_url)
        mIntent.putExtra("data", dataJson.toString())
        mActivity.startActivity(mIntent)
    }

    private fun onSettingWebCoverPosition() {
        val xPosition = Constants.onGetWebViewPosition()
        if (xPosition.width > 0 && xPosition.left > 0) {
            val iWebCoverLayout = iWebCover.layoutParams as FrameLayout.LayoutParams
            iWebCoverLayout.topMargin = Constants.dip2px(iWebCover.context, xPosition.top.toFloat())
            iWebCoverLayout.leftMargin = Constants.dip2px(iWebCover.context, xPosition.left.toFloat())
            iWebCoverLayout.width = Constants.dip2px(iWebCover.context, xPosition.width.toFloat())
            iWebCoverLayout.height = Constants.dip2px(iWebCover.context, xPosition.height.toFloat())
            iWebCover.layoutParams = iWebCoverLayout
        }
    }

    //点击后退
    private fun onBackPress() {
        mActivity.finish()
//        when (zsAuthType) {
//            1 -> when (zsStatus) {
//                2 -> xMessageDialog?.setTitle("任务已经被接受")?.setContent("您的授权任务已被接受，无法取消请等待！")
//                    ?.addConfirmListener(View.OnClickListener { xMessageDialog?.dismiss() })?.show()
//                else -> xMessageDialog?.setTitle("取消任务")?.setContent("您确定要取消任务？")
//                    ?.addConfirmListener(View.OnClickListener {
//                        showZSLayout(false)
//                        GlobalScope.launch(Dispatchers.Main) {
//                            mViewModel?.onCancelAuth(authOrderId!!)
//                                ?.observe(mLifeOwner, androidx.lifecycle.Observer {
//                                    mWebSocket?.close()
//                                })
//                        }
//                        xMessageDialog?.dismiss()
//                        mActivity.finish()
//                    })?.show()
//            }
//            else -> mActivity.finish()
//        }
    }

    fun onDestroy() {
        mProgressDialog?.dismiss()
        xMessageDialog?.dismiss()
        mWebSocket?.close()
    }

//    @SuppressLint("NewApi")
//    fun isRunningForegroundToApp1(context: Context,packageName: String) {
//        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//        val taskInfoList = activityManager.getRunningTasks(20)
//        /**枚举进程 */
//        for (taskInfo in taskInfoList) {
//            //*找到本应用的 task，并将它切换到前台
//            if (taskInfo.baseActivity!!.packageName == packageName) {
//                activityManager.moveTaskToFront(taskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME)
//                break
//            }
//        }
//    }

}
