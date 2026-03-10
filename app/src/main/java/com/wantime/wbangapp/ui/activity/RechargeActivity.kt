package com.wantime.wbangapp.ui.activity

import androidx.fragment.app.Fragment
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseActivity
import com.wantime.wbangapp.ui.fragment.RechargeFragment
import kotlin.properties.Delegates

class RechargeActivity : BaseActivity() {

    private var mRechargeFragment: RechargeFragment by Delegates.notNull()

    override val layoutId: Int
        get() = R.layout.activity_fragment

    override fun afterInitView() {
        initFragment()
    }

    private fun initFragment() {
        mRechargeFragment = RechargeFragment()
        if (mParamJson != null)
            mRechargeFragment.setParams(mParamJson!!)
        initFragment(mRechargeFragment as Fragment, R.id.frameLayout)
    }
}