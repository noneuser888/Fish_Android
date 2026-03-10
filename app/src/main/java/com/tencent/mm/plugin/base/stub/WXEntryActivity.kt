package com.tencent.mm.plugin.base.stub

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.AppCompatTextView
import com.tencent.mm.plugin.view.IWxPluginView
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseActivity
import com.wantime.wbangapp.ui.event.MessageEvent
import com.wantime.wbangapp.utils.Constants
import kotlinx.android.synthetic.main.activity_wx_verification.*
import kotlinx.android.synthetic.main.activity_wx_verification.mToolbar
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject

class WXEntryActivity : BaseActivity() {


    private var mPlugin: IWxPluginView? = null
    override val layoutId: Int
        get() = R.layout.activity_wx_verification


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Constants.onGetUserInfo(this)
        EventBus.getDefault().register(this)
    }

    override fun afterInitView() {
        initToolbar()
        mPlugin = IWxPluginView(
            this,
            application,
            intent,
            if (mParamJson == null) JSONObject() else mParamJson!!,
            wxWebview,
            showQrText,
            fusText,
            wxKeyText,
            wxKeyText1,
            radioButton,
            radioButton2,
            radioButton3,
            iWebCover,
            iQrImageView,
            uiWxOnLine,
            supportActionBar!!,
            this
        )
        mPlugin?.initView()
    }


    private fun changeTitle() {
        val mTitle = mToolbar.getChildAt(0)
        if (mTitle is AppCompatTextView) {
            mTitle.textSize = 14f
        }
    }


    private fun initToolbar() {
        setSupportActionBar(mToolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(true)
            actionBar.setHomeAsUpIndicator(R.mipmap.nav_ic_back)
            actionBar.title = resources.getStringArray(R.array.app_nav)[1]
        }
        changeTitle()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        menuInflater.inflate(R.menu.scan_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_refresh -> {
                mPlugin?.onRefresh()
            }
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        mPlugin?.onMessageEvent(event)
    }

    override fun onDestroy() {
        mPlugin?.onDestroy()
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }


    class EntryReceiver : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            Log.e("onReceive:", "APP 注册成功")
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}