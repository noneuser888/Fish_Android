package com.wantime.wbangapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseActivity
import com.wantime.wbangapp.model.AdItemBean
import com.wantime.wbangapp.ui.adapter.AdvoicePagerAdapter
import com.wantime.wbangapp.ui.adapter.RecyclerItemClick
import com.wantime.wbangapp.utils.Constants
import com.wantime.wbangapp.utils.ITimer
import com.wantime.wbangapp.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.activity_welcome.*
import org.json.JSONObject
import kotlin.properties.Delegates

class WelComeActivity : BaseActivity() {
    private var mLoginModel: LoginViewModel? = null
    private val jumpCount = 5
    private var jumpNumber = 0
    private var isHolder = false
    private var mITimer: ITimer by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0) {
            finish();return; }
    }

    override val layoutId: Int
        get() = R.layout.activity_welcome

    override fun afterInitView() {
        jumpButton.setOnClickListener {
            jumpButton.isEnabled = false
            mITimer.stop()
            onStartMain()
        }
        mLoginModel = LoginViewModel(application)
        //启动广告
        mLoginModel?.onAdLaunch()?.observe(this, Observer {
            if (it.imgList != null && it.imgList.size > 0) {
                initAdaPager(it.imgList as ArrayList<AdItemBean.ImgListBean>)
            }
        })

        mITimer = ITimer.getTimer().onPeriod(1000).onStart(Runnable {
            runOnUiThread {
                jumpNumber++
                if (jumpNumber >= jumpCount && !isHolder) {
                    mITimer.stop()
                    onStartMain()
                }
            }
        })
    }

    override fun onResume() {
        isHolder = false
        super.onResume()
    }

    private fun initAdaPager(dataList: ArrayList<AdItemBean.ImgListBean>) {
        runOnUiThread {
            val mPageAdapter =
                AdvoicePagerAdapter<AdItemBean.ImgListBean>(object : RecyclerItemClick {
                    override fun onItemClick(itemView: View, iBean: Any) {
                        onPagerItemClick(itemView, iBean)
                    }
                })
            mViewPager.adapter = mPageAdapter
            mPageAdapter.pushDataList(dataList)
            mPageAdapter.notifyDataSetChanged()
        }
    }

    private fun onStartMain() {
        if (Constants.isLogin(this))
            startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun onPagerItemClick(itemView: View, iBean: Any) {
        val iItemBean = iBean as AdItemBean.ImgListBean
        if (!TextUtils.isEmpty(iItemBean.webLink)) {
            isHolder = true
            val mIntent = Intent(this, IWebActivity::class.java)
            val mJson = JSONObject()
            mJson.put("webUrl", iItemBean.webLink)
            mIntent.putExtra("data", mJson.toString())
            startActivity(mIntent)
        }
    }

    override fun onDestroy() {
        mITimer.stop()
        super.onDestroy()
    }

}