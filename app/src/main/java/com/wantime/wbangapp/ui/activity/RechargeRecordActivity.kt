package com.wantime.wbangapp.ui.activity

import androidx.fragment.app.Fragment
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseActivity
import com.wantime.wbangapp.ui.fragment.RechargeRecordFragment
import kotlin.properties.Delegates

class RechargeRecordActivity : BaseActivity() {

    private var mAgentManagerFragment: RechargeRecordFragment by Delegates.notNull()

    override val layoutId: Int
        get() = R.layout.activity_fragment

    override fun afterInitView() {
        initFragment()
    }

    private fun initFragment() {
        mAgentManagerFragment = RechargeRecordFragment()
        if (mParamJson != null)
            mAgentManagerFragment.setParams(mParamJson!!)
        initFragment(mAgentManagerFragment as Fragment, R.id.frameLayout)
    }
}