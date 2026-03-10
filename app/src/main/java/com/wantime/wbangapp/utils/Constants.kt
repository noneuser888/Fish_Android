package com.wantime.wbangapp.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.alibaba.fastjson.JSON
import com.block.dog.common.util.ToastUtil
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.tencent.mm.plugin.base.stub.WXEntryActivity
import com.uuzuche.lib_zxing.activity.CodeUtils
import com.uuzuche.lib_zxing.activity.CodeUtils.AnalyzeCallback
import com.uuzuche.lib_zxing.camera.BitmapLuminanceSource
import com.uuzuche.lib_zxing.decoding.DecodeFormatManager
import com.wantime.wbangapp.R
import com.wantime.wbangapp.database.SQLHelper
import com.wantime.wbangapp.inter.IObserver
import com.wantime.wbangapp.model.PositionBean
import com.wantime.wbangapp.model.UserBean
import com.wantime.wbangapp.model.bind.IBindUserBean
import com.wantime.wbangapp.request.RetrofitManager
import com.wantime.wbangapp.request.ValueCallBack
import com.wantime.wbangapp.ui.activity.*
import com.wantime.wbangapp.ui.event.MessageEvent
import droidninja.filepicker.FilePickerBuilder
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.io.File
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.util.*


/**
 *  on 2018/12/26.
 */
object Constants {

    //const val baseAPIUrl = "http://192.168.0.115:8082/"
    //const val baseAPIUrl = "http://www.pengyuyan.top:8082/"
    //const val commonProblemUrl = "http://www.pengyuyan.top:8082/faq"
    //const val webSocket = "wss://www.pengyuyan.top:8082/socket/{userId}"

    const val baseAPIUrl = "http://app.51juzi.xyz/"
    const val commonProblemUrl = "http://www.cn-propy.com:8082/faq"
    const val webSocket = "wss://app.51juzi.xyz/socket/{userId}"

    private var mUserBaseInfoWithToken: JSONObject? = null
    private var mUserBaseInfoWithNoToken: JSONObject? = null
    private var mWebViewPosition: JSONObject? = null
    val filePath = Environment.getExternalStorageDirectory().toString() + "/wBang/"
    private var DIP2PXSCALE = 0f
    const val NET_OK = 20000
    const val NET_FAILED = 20001
    const val NET_APPLY_PLATFORM = 20006//重新申请凭条
    const val NET_REAL_NAME = 20008//需要实名认证
    const val CUSTOM_REQUEST_CODE = 1
    const val CUSTOM_REQUEST_IMAGE = 2
    const val CUSTOM_REQUEST_WX = 2

    @SuppressLint("SimpleDateFormat")
    val format = SimpleDateFormat("yyyy/MM/dd")
    private var appParamJson: JSONObject? = null

    @kotlin.jvm.JvmField
    var accountType = 1//默认使用普通账号授权
    fun createTempFiles() {
        val tempFile = File(filePath)
        if (!tempFile.exists()) {
            tempFile.mkdirs()
        }
    }

    fun dip2px(context: Context, dipValue: Float): Int {
        if (dipValue <= 0) return 0
        if (DIP2PXSCALE <= 0) DIP2PXSCALE = context.resources.displayMetrics.density
        return (dipValue * DIP2PXSCALE + 0.5f).toInt()
    }

    fun isMainThread(): Boolean {
        return Looper.getMainLooper() == Looper.myLooper()
    }

    //获取用户基本信息
    fun initUserInfo(context: Context) {
        if (mUserBaseInfoWithToken == null) {
            mUserBaseInfoWithToken = SQLHelper.getInstance(context).getSqlData(TableUtils.userTable)
        }
        accountType = getRealInt(getSystemParam(context,"accountType").toString())
        if (accountType < 1) accountType = 1
    }

    //获取用户信息
    fun onGetUserInfo(context: Context): IBindUserBean {
        val xUserBean = IBindUserBean()
        if (mUserBaseInfoWithToken == null) {
            mUserBaseInfoWithToken = SQLHelper.getInstance(context).getSqlData(TableUtils.userTable)
        }
        if (mUserBaseInfoWithToken != null && mUserBaseInfoWithToken!!.length() > 0) {
            val tempUser = JSON.parseObject(mUserBaseInfoWithToken.toString(), UserBean::class.java)
            xUserBean.password.set(tempUser.password)
            xUserBean.phone.set(tempUser.phone)
        }
        return xUserBean
    }

    //自动登录方法
    fun onAutoLogin(context: Context) {
        if (mUserBaseInfoWithToken == null) {
            mUserBaseInfoWithToken = SQLHelper.getInstance(context).getSqlData(TableUtils.userTable)
        }
        if (mUserBaseInfoWithToken != null && mUserBaseInfoWithToken!!.length() > 0) {
            val tempUser = JSON.parseObject(mUserBaseInfoWithToken.toString(), UserBean::class.java)
            RetrofitManager.getInstance().apiRequest.onLogin(tempUser).subscribeOn(Schedulers.io())
                .subscribe(object : IObserver<String>() {
                    override fun onNext(t: String) {
                        onGetInfo(t).apply {
                            when (this.optInt("code")) {
                                NET_OK -> {
                                    val baseInfo = getJSONObject("data")
                                    if (baseInfo.optInt("status", 1) == 0) {
                                        baseInfo.put("account", tempUser.phone)
                                        baseInfo.put("password", tempUser.password)
                                        onSaveUserBaseInfoWithToken(context,baseInfo)
                                    }
                                }
                            }
                        }

                    }

                    override fun onError(e: Throwable) {
                    }
                })
        }

    }

    //保存用户信息
    fun onSaveUserInfo(context: Context,mUserBean: UserBean) {
        try {
            mUserBaseInfoWithToken = JSONObject(JSON.toJSONString(mUserBean))
            SQLHelper.getInstance(context).saveSqlData(TableUtils.userTable, mUserBaseInfoWithToken)
        } catch (ex: Exception) {
            Log.e("ex", ex.toString())
        }
    }

    //记录用户其他的信息带Token
    fun onSaveUserBaseInfoWithToken(context: Context,dataJson: JSONObject) {
        mUserBaseInfoWithToken = dataJson
        SQLHelper.getInstance(context).saveSqlData(TableUtils.userTable, mUserBaseInfoWithToken)
    }

    //记录位置
    fun onSaveWebViewPosition(context: Context,dataJson: JSONObject) {
        mWebViewPosition = dataJson
        SQLHelper.getInstance(context).saveSqlData(TableUtils.positionTable, mWebViewPosition)
    }

    fun onSaveUserBaseInfoWithNoToken(dataJson: JSONObject) {
        mUserBaseInfoWithNoToken = dataJson
    }

    //获取用户数据
    fun onGetWebViewPosition(): PositionBean {
        try {
            return JSON.parseObject(mWebViewPosition.toString(), PositionBean::class.java)
        } catch (ex: java.lang.Exception) {
        }
        return PositionBean()
    }

    //基本信息
    fun onGetUserBaseInfoWithToken(): UserBean {
        try {
            return JSON.parseObject(mUserBaseInfoWithToken.toString(), UserBean::class.java)
        } catch (ex: java.lang.Exception) {
        }
        return UserBean()
    }

    //基本信息
    fun onGetUserBaseInfoWithNoToken(): UserBean {
        try {
            return JSON.parseObject(mUserBaseInfoWithNoToken.toString(), UserBean::class.java)
        } catch (ex: java.lang.Exception) {
        }
        return UserBean()
    }

    //获取返回信息
    fun onGetInfo(message: String): JSONObject {
        try {
            return JSONObject(message)
        } catch (e: java.lang.Exception) {
        }
        return birthErrorJson()
    }

    //获取用户是否登录过呢
    fun isLogin(mActivity: Context): Boolean {
        val loginBean = onGetUserBaseInfoWithToken()
        val isLoginStated = TextUtils.isEmpty(loginBean.token)
        if (isLoginStated) mActivity.startActivity(Intent(mActivity, LoginActivity::class.java))
        return !isLoginStated
    }

    //跳转登录界面
    fun openLogin(mActivity: Activity) {
        mActivity.startActivity(Intent(mActivity, LoginActivity::class.java))
        mActivity.finish()
    }

    //清理登录状态
    fun clearLogin(context: Context) {
        mUserBaseInfoWithToken = null
        mUserBaseInfoWithNoToken = null
        onSaveUserInfo(context,UserBean())
    }

    //打开扫描器
    fun openCaptureByFragment(
        mFragment: Fragment,
        mParamJson: String,
        title: String,
        viewType: Int
    ) {
        val mIntent = Intent(mFragment.context, ICaptureActivity::class.java)
        val mJson = JSONObject(mParamJson)
        mJson.put("title", title)
        mJson.put("viewType", viewType)
        mIntent.putExtra("data", mJson.toString())
        mFragment.startActivityForResult(mIntent, CUSTOM_REQUEST_CODE)
    }

    //打开授权认证
    fun openWxAuth(mFragment: Fragment, mParamJson: String) {
        val mIntent = Intent(mFragment.context, WXEntryActivity::class.java)
        val mJson = JSONObject(mParamJson)
        mIntent.putExtra("data", mJson.toString())
        mFragment.startActivityForResult(mIntent, CUSTOM_REQUEST_WX)
    }

    private fun birthErrorJson(): JSONObject {
        val errorJson = JSONObject()
        errorJson.put("code", 0)
        errorJson.put("flag", false)
        return errorJson
    }

    //打开文件
    fun openFilePicker(mFragment: Fragment) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        mFragment.startActivityForResult(intent, CUSTOM_REQUEST_CODE)
    }

    fun openMoreFilePicker(mFragment: Fragment, photoPaths: ArrayList<Uri>, maxCount: Int) {
        FilePickerBuilder.instance
            .setMaxCount(maxCount)
            .setSelectedFiles(photoPaths) //this is optional
            .setActivityTheme(R.style.FilePickerTheme)
            .setActivityTitle("Please select media")
            .enableVideoPicker(true)
            .enableCameraSupport(true)
            .showGifs(true)
            .showFolderView(true)
            .enableSelectAll(false)
            .enableImagePicker(true)
            .setCameraPlaceholder(R.drawable.custom_camera)
            .withOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .pickPhoto(mFragment, CUSTOM_REQUEST_CODE)
    }

    fun openMoreFilePicker(mActivity: Activity, photoPaths: ArrayList<Uri>, maxCount: Int) {
        FilePickerBuilder.instance
            .setMaxCount(maxCount)
            .setSelectedFiles(photoPaths) //this is optional
            .setActivityTheme(R.style.FilePickerTheme)
            .setActivityTitle("Please select media")
            .enableVideoPicker(true)
            .enableCameraSupport(true)
            .showGifs(true)
            .showFolderView(true)
            .enableSelectAll(false)
            .enableImagePicker(true)
            .setCameraPlaceholder(R.drawable.custom_camera)
            .withOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .pickPhoto(mActivity, CUSTOM_REQUEST_CODE)
    }

    //打开WEB
    fun openWebView(mActivity: Activity, title: String, webUrl: String) {
        val mJson = JSONObject()
        mJson.put("title", title)
        mJson.put("webUrl", webUrl)
        val mIntent = Intent(mActivity, IWebActivity::class.java)
        mIntent.putExtra("data", mJson.toString())
        mActivity.startActivity(mIntent)
    }

    fun openFloatWebView(mActivity: Activity, title: String, webUrl: String) {
        val mJson = JSONObject()
        mJson.put("title", title)
        mJson.put("webUrl", webUrl)
        val mIntent = Intent(mActivity, IFloatWebActivity::class.java)
        mIntent.putExtra("data", mJson.toString())
        mActivity.startActivity(mIntent)
    }

    fun isHttpLink(url: String): Boolean {
        return !TextUtils.isEmpty(url) && (url.toLowerCase().startsWith("http") || url.toLowerCase()
            .startsWith("https"))
    }

    //识别本地图片的二维信息
    fun onGetQrInfo(mBitmap: Bitmap, valueCallBack: ValueCallBack) {
        onAnalyzeBitmap(mBitmap, object : AnalyzeCallback {
            override fun onAnalyzeSuccess(mBitmap: Bitmap, result: String) {
                valueCallBack.onValueBack(result)
            }

            override fun onAnalyzeFailed() {
                valueCallBack.onValueBack("")
            }
        })
    }

    //设置View 左右drawable
    fun onSetDrawableLeft(context: Context, textDrawable: TextView, resID: Int) {
        val drawableLeft: Drawable = context.resources.getDrawable(resID)
        textDrawable.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null)
    }


    fun getUserKeys(): String {
        return "netWorkLink"
    }

    private fun onAnalyzeBitmap(mBitmap: Bitmap, analyzeCallback: AnalyzeCallback) {
        val multiFormatReader = MultiFormatReader()
        // 解码的参数
        val hints =
            Hashtable<DecodeHintType, Any?>(2)
        // 可以解析的编码类型
        var decodeFormats =
            Vector<BarcodeFormat?>()
        if (decodeFormats.isEmpty()) {
            decodeFormats = Vector()

            // 这里设置可扫描的类型，我这里选择了都支持
            decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS)
            decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS)
            decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS)
        }
        hints[DecodeHintType.POSSIBLE_FORMATS] = decodeFormats
        // 设置继续的字符编码格式为UTF8
        // hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
        // 设置解析配置参数
        multiFormatReader.setHints(hints)

        // 开始对图像资源解码
        var rawResult: Result? = null
        try {
            rawResult = multiFormatReader.decodeWithState(
                BinaryBitmap(
                    HybridBinarizer(
                        BitmapLuminanceSource(mBitmap)
                    )
                )
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        if (rawResult != null) {
            analyzeCallback.onAnalyzeSuccess(mBitmap, rawResult.text)
        } else {
            analyzeCallback.onAnalyzeFailed()
        }
    }

    fun OnBirthQrImage(imageView: ImageView, textContent: String) {
        val imageWidth = dip2px(imageView.context, 180f)
        val mBitmap = CodeUtils.createImage(textContent, imageWidth, imageWidth, null)
        imageView.setImageBitmap(mBitmap)
    }

    fun onGetWxOpenLink(
        appId: String,
        appPackage: String,
        scope: String,
        state: String,
        type: Int = 1
    ): String {
        val mBuffer = StringBuffer()
        val tempAppId = getRightAppId(appId)
        if (type == 1) {
            mBuffer.append("h").append("t").append("tps")
                .append(":").append("//")
                .append("op").append("en").append(".").append("we")
                .append("ix").append("in.").append("q").append("q.")
                .append("com").append("/con").append("nect").append("/app/qr")
                .append("connect?app").append("id=").append(tempAppId).append("&bund")
                .append("leid=").append(appPackage).append("&").append(scope);
        } else {
            mBuffer.append("h").append("t").append("tps")
                .append(":").append("//")
                .append("op").append("en").append(".").append("we")
                .append("ix").append("in.").append("q").append("q.")
                .append("com").append("/con").append("nect").append("/app/qr")
                .append("connect?app").append("id=").append(tempAppId).append("&bund")
                .append("leid=").append(appPackage).append("&scope=").append(scope)
                .append("&state=").append(state);
        }


        //"https://open.weixin.qq.com/connect/app/qrconnect?appid=$appId&bundleid=$appPackage&$scope"
        return mBuffer.toString()
    }

    //有些appID可能大于18则只取18
    private fun getRightAppId(appId: String): String {
        var xAppId = appId
        if (!TextUtils.isEmpty(appId) && appId.length > 18) {
            xAppId = appId.substring(0, 18)
        }
        return xAppId
    }


    fun isExistApplication(context: Context, packageName: String?): Boolean {
        val packageManager: PackageManager = context.packageManager
        //获取系统中安装的应用包的信息
        val listPackageInfo: List<PackageInfo> = packageManager.getInstalledPackages(0)
        for (element in listPackageInfo) {
            if (element.packageName.toLowerCase(Locale.ROOT) == packageName) {
                return true
            }
        }
        return false
    }

    fun setAppParamJson(appParamJson1: JSONObject?) {
        appParamJson = appParamJson1
    }

    fun getAppParamJson(): JSONObject? {
        return appParamJson
    }

    fun onGetState(scope: String): String {
        try {
            if (!TextUtils.isEmpty(scope) && scope.contains("state=")) {
                val mIndex = scope.indexOf("state=")
                return scope.substring(mIndex + 7)
            }
        } catch (ex: Exception) {
        }
        return ""
    }


    fun onGetTimeStamp(timeStr: String): Long {
        try {
            return format.parse(timeStr).time
        } catch (ex: java.lang.Exception) {
        }

        return 0L
    }

    fun onGotoIdCardVerif(mActivity: Activity) {
        mActivity.startActivity(Intent(mActivity, RealNameActivity::class.java))
    }


    fun sendRefreshCmd() {
        val eventMessage = MessageEvent()
        eventMessage.mType = MessageEvent.EventType.refreshFragment.ordinal
        EventBus.getDefault().post(eventMessage)
    }

    fun sendRefreshUserBaseInfo() {
        val eventMessage = MessageEvent()
        eventMessage.mType = MessageEvent.EventType.requestAgainBaseInfo.ordinal
        EventBus.getDefault().post(eventMessage)
    }

    fun sendLoginException() {
        val eventMessage = MessageEvent()
        eventMessage.mType = MessageEvent.EventType.loginException.ordinal
        EventBus.getDefault().post(eventMessage)
    }

    fun sendCloseMain() {
        val eventMessage = MessageEvent()
        eventMessage.mType = MessageEvent.EventType.closeMain.ordinal
        EventBus.getDefault().post(eventMessage)
    }


    fun onGetMenuOpenAuthority(menuPosition: Int): Boolean {
        val iBean = onGetUserBaseInfoWithNoToken()
        var isCanMenuOpen = false
        when (iBean.level) {
            "1" -> isCanMenuOpen = true
            "2" -> if (menuPosition == 1) isCanMenuOpen = true
        }

        return isCanMenuOpen
    }

    fun onURLDecoder(webUrl: String): String {
        var tempUrl = webUrl
        try {
            tempUrl = tempUrl.replace("%(?![0-9a-fA-F]{2})", "%25");
            tempUrl = URLDecoder.decode(tempUrl, "UTF-8")
        } catch (ex: java.lang.Exception) {
        }

        return tempUrl
    }

    fun getRealInt(number: String): Int {
        var tempNumber = 0
        try {
            tempNumber = Integer.parseInt(number)
        } catch (ex: java.lang.Exception) {
        }
        return tempNumber
    }

    /**
     * 获取系统参数
     * ***/
    private fun getSystemParam(context: Context,sysParamKey: String): Any {
        val mJson = SQLHelper.getInstance(context).getSqlData(TableUtils.systemTable)
        if (mJson.has(sysParamKey)) return mJson.get(sysParamKey)
        return "1"
    }

    /**
     * 保存参数
     * **/
    fun setSystemParam(context:Context,sysParamKey: String, anyOb: Any) {
        val mJson = SQLHelper.getInstance(context).getSqlData(TableUtils.systemTable)
        mJson.put(sysParamKey, anyOb)
        SQLHelper.getInstance(context).saveSqlData(TableUtils.systemTable, mJson)
    }
}