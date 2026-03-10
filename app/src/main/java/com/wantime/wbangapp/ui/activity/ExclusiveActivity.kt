package com.wantime.wbangapp.ui.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseActivity
import com.wantime.wbangapp.ui.event.MessageEvent
import com.wantime.wbangapp.ui.fragment.ExclusiveFragment
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

//专属授权
class ExclusiveActivity :BaseActivity() {
    private var mExclusiveFragment: ExclusiveFragment?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override val layoutId: Int
        get() = R.layout.activity_fragment

    override fun afterInitView() {
        requestPermission()
        initFragment()
    }

    private fun initFragment(){
        mExclusiveFragment= ExclusiveFragment()
        initFragment(mExclusiveFragment as Fragment, R.id.frameLayout)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        when (event.mType) {
            MessageEvent.EventType.refreshFragment.ordinal -> {
                mExclusiveFragment?.onRefresh()
            }
        }

    }
    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
}