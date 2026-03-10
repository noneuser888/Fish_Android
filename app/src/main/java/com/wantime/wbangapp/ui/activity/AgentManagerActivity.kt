package com.wantime.wbangapp.ui.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseActivity
import com.wantime.wbangapp.ui.event.MessageEvent
import com.wantime.wbangapp.ui.fragment.AgentManagerFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.properties.Delegates

class AgentManagerActivity : BaseActivity() {

    private var mAgentManagerFragment: AgentManagerFragment by Delegates.notNull()
    override fun onCreate(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        super.onCreate(savedInstanceState)
    }

    override val layoutId: Int
        get() = R.layout.activity_fragment

    override fun afterInitView() {
        initFragment()
    }

    private fun initFragment() {
        mAgentManagerFragment = AgentManagerFragment()
        if (mParamJson != null)
            mAgentManagerFragment.setParams(mParamJson!!)
        initFragment(mAgentManagerFragment as Fragment, R.id.frameLayout)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        when (event.mType) {
            MessageEvent.EventType.refreshFragment.ordinal -> {
                mAgentManagerFragment.onRefresh()
            }
        }

    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
}