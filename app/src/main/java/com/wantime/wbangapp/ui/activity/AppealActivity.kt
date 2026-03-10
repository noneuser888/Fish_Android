package com.wantime.wbangapp.ui.activity

import androidx.fragment.app.Fragment
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseActivity
import com.wantime.wbangapp.ui.fragment.AppealFragment

class AppealActivity : BaseActivity() {

    private var mAppealFragment: AppealFragment? = null

    override val layoutId: Int
        get() = R.layout.activity_fragment

    override fun afterInitView() {
        initFragment()
    }

    private fun initFragment() {
        mAppealFragment = AppealFragment.newInstance()
        if (mParamJson != null)
            mAppealFragment?.setParams(mParamJson!!)
        initFragment(mAppealFragment as Fragment, R.id.frameLayout)
    }
}