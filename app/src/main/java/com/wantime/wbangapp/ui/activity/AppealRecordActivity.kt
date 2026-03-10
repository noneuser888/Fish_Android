package com.wantime.wbangapp.ui.activity

import androidx.fragment.app.Fragment
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseActivity
import com.wantime.wbangapp.ui.fragment.AppealFragment
import com.wantime.wbangapp.ui.fragment.AppealRecordFragment

class AppealRecordActivity : BaseActivity() {

    private var mAppealRecordFragment: AppealRecordFragment? = null

    override val layoutId: Int
        get() = R.layout.activity_fragment

    override fun afterInitView() {
        initFragment()
    }

    private fun initFragment() {
        mAppealRecordFragment = AppealRecordFragment.newInstance()
        initFragment(mAppealRecordFragment as Fragment, R.id.frameLayout)
    }
}