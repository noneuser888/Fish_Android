package com.wantime.wbangapp.ui.activity

import android.R.attr
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.block.dog.common.util.ToastUtil
import com.uuzuche.lib_zxing.activity.CaptureFragment
import com.uuzuche.lib_zxing.activity.CodeUtils
import com.uuzuche.lib_zxing.activity.CodeUtils.AnalyzeCallback
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseActivity
import com.wantime.wbangapp.ui.dialog.IProgressLoading
import com.wantime.wbangapp.ui.dialog.MessageDialog
import com.wantime.wbangapp.ui.event.MessageEvent
import com.wantime.wbangapp.utils.Constants
import com.wantime.wbangapp.viewmodel.WXAuthViewModel
import droidninja.filepicker.FilePickerConst
import droidninja.filepicker.utils.ContentUriUtils
import kotlinx.android.synthetic.main.activity_wx_verification.*
import kotlinx.android.synthetic.main.ui_fragment_icapture_tools.*
import kotlinx.android.synthetic.main.ui_top_navigation.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject


class ICaptureActivity : BaseActivity() {

    private var captureFragment: CaptureFragment? = null
    private var messageDialog: MessageDialog? = null

    private var mIsPCCaptureType = false
    private var photoPaths = ArrayList<Uri>()
    private var isLightOpen = false
    private var mViewModel: WXAuthViewModel? = null
    private var mProgressDialog: IProgressLoading? = null
    private var wxapi_sendauth_req_state=""

    private var isInit = false
    private var appId = ""

    //复扫的订单ID
    private var orderId = ""
    private var  oneKeyType=1 //一键授权类型

    override val layoutId: Int
        get() = R.layout.activity_fragment_with_nav

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun afterInitView() {
        mViewModel = WXAuthViewModel(application)
        initView()
        initCaptureFragment()
    }


    private fun initView() {
        messageDialog = MessageDialog(this)
            .setTitle(getString(R.string.ui_authority_title))
            .setContent(getString(R.string.ui_authority_content))
        initAfterView()
    }

    private fun openRecord() {
        if (mParamJson != null) {
            val mIntent = Intent(this, SingleRecordActivity::class.java)
            mIntent.putExtra("data", mParamJson.toString())
            startActivity(mIntent)
        }
    }

    private fun initCaptureFragment() {
        // 为二维码扫描界面设置定制化界面
        captureFragment = CaptureFragment()
        CodeUtils.setFragmentArgs(captureFragment, R.layout.fragment_icapture)
        captureFragment?.analyzeCallback = analyzeCallback
        initFragment(captureFragment as Fragment, R.id.frameLayout)
    }


    private fun initAfterView() {
        navBack.visibility = View.VISIBLE
        navRightIcon1.visibility=View.GONE
        navBack.setOnClickListener { finish() }
        navRightIcon1.text = getString(R.string.ui_saomiao_change)
        navRightIcon.text = getString(R.string.ui_saomiao_repeat)
        oldOrNewGroup.setOnCheckedChangeListener { _, checkedId ->
            oneKeyType = when(checkedId){
                R.id.mOldButton-> 2
                else-> 1
            }

            Log.e("oneKeyType",">>>>>>>>>>>${oneKeyType}")
        }

        navRightIcon.setOnClickListener { openRecord() }
        navRightIcon1.setOnClickListener { onChange() }
        iPhoto.setOnClickListener { onScanLocal() }
        iFlash.setOnClickListener { openLight() }
        if (mParamJson != null) {
            Log.e("mParamJson",mParamJson.toString())
            appId = mParamJson!!.optString("appId", "")
            wxapi_sendauth_req_state=Constants.onGetState(mParamJson!!.optString("scope",""))
            navTitle.text = mParamJson!!.optString("title")
            val viewType = mParamJson!!.optInt("viewType", 0)
            navRightIcon1.visibility = if (viewType == 0) View.VISIBLE else View.GONE
            navRightIcon.visibility = if (viewType == 0) View.VISIBLE else View.GONE
        }
    }


    private fun onChange() {
        messageDialog?.addConfirmListener(View.OnClickListener {
            mIsPCCaptureType = !mIsPCCaptureType
            toolTips.text =
                if (mIsPCCaptureType) getString(R.string.ui_authority_pc) else getString(R.string.ui_authority_app)
            messageDialog?.dismiss()
        })
        messageDialog?.show()
    }

    private fun openLight() {
        isLightOpen = !isLightOpen
        CodeUtils.isLightEnable(isLightOpen)
    }

    private fun showProgress() {
        mProgressDialog?.run { mProgressDialog?.show() }
    }

    private fun hiddenProgress() {
        mProgressDialog?.run { mProgressDialog?.dismiss() }
    }

    /**
     * 二维码解析回调函数
     */
    var analyzeCallback: AnalyzeCallback = object : AnalyzeCallback {
        override fun onAnalyzeSuccess(mBitmap: Bitmap, result: String) {
            onScanQrCode(mBitmap, result)
//            val resultIntent = Intent()
//            val bundle = Bundle()
//            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS)
//            bundle.putString(CodeUtils.RESULT_STRING, result)
//            resultIntent.putExtras(bundle)
//            this@ICaptureActivity.setResult(Activity.RESULT_OK, resultIntent)
//            this@ICaptureActivity.finish()
        }

        override fun onAnalyzeFailed() {
            ToastUtil.show(this@ICaptureActivity, "解析二维码失败")
//            val resultIntent = Intent()
//            val bundle = Bundle()
//            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED)
//            bundle.putString(CodeUtils.RESULT_STRING, "")
//            resultIntent.putExtras(bundle)
//            this@ICaptureActivity.setResult(Activity.RESULT_OK, resultIntent)
//            this@ICaptureActivity.finish()
        }
    }

    //扫描本地图片
    private fun onScanLocal() {
//        val intent = Intent(Intent.ACTION_GET_CONTENT)
//        intent.addCategory(Intent.CATEGORY_OPENABLE)
//        intent.type = "image/*"
//        startActivityForResult(intent, Constants.CUSTOM_REQUEST_IMAGE)
        Constants.openMoreFilePicker(this, photoPaths, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && Constants.CUSTOM_REQUEST_CODE == requestCode) {
            if (data != null) {
                val dataList =
                    data.getParcelableArrayListExtra<Uri>(FilePickerConst.KEY_SELECTED_MEDIA)
                if (dataList != null) {
                    val imagePath = dataList[0]
                    val mBitmap = ContentUriUtils.getFilePath(this, imagePath)
                    CodeUtils.analyzeBitmap(mBitmap, object : AnalyzeCallback {
                        override fun onAnalyzeSuccess(mBitmap: Bitmap, result: String) {
                            onScanQrCode(mBitmap, result)
                        }

                        override fun onAnalyzeFailed() {
                            ToastUtil.show(this@ICaptureActivity, "解析二维码失败")
                        }
                    })
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        when (event.mType) {
            MessageEvent.EventType.reaptScan.ordinal -> {
                try {
                    val mResult = JSONObject(event.dataJson)
                    orderId = if(mResult.has("id")) mResult.optString("id") else mResult.optString("Id")
                    val tempText = navTitle.text.toString()
                    if (!tempText.contains("复扫"))
                        navTitle.text = "$tempText（复扫）"
                } catch (ex: java.lang.Exception) {
                }
            }
        }
    }

    private fun onScanQrCode(mBitmap: Bitmap, result: String) {
        mBitmap.recycle()
        when (mIsPCCaptureType) {
            true -> onPCAuthorityRequest(result, appId, orderId)
            else -> onKeyAuthorityRequest(result, appId, orderId)
        }
    }

    private fun onKeyAuthorityRequest(qrCode: String, appId: String, orderId: String?) {
        showProgress()
        GlobalScope.launch(Dispatchers.Main) {
            mViewModel?.onKeyAuthorityRequest(
                qrCode, appId, if (TextUtils.isEmpty(orderId)) "" else orderId!!
                ,wxapi_sendauth_req_state,oneKeyType,2,Constants.accountType
            )?.observe(this@ICaptureActivity, androidx.lifecycle.Observer {
                if (TextUtils.isEmpty(it.authOrderId)) {
                    hiddenProgress()
                    ToastUtil.show(this@ICaptureActivity, getString(R.string.ui_authority_failed))
                } else {
                    finish()
                }
            })
        }
    }

    private fun onPCAuthorityRequest(qrCode: String, appId: String, orderId: String?) {
        showProgress()
        GlobalScope.launch(Dispatchers.Main) {
            mViewModel?.onPCAuthorityRequest(
                qrCode, appId, if (TextUtils.isEmpty(orderId)) "" else orderId!!,wxapi_sendauth_req_state
            )?.observe(this@ICaptureActivity, androidx.lifecycle.Observer {
                if (TextUtils.isEmpty(it.authOrderId)) {
                    hiddenProgress()
                    ToastUtil.show(this@ICaptureActivity, getString(R.string.ui_authority_failed))
                } else {
                    finish()
                }
            })
        }
    }


    override fun onDestroy() {
        hiddenProgress()
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
}