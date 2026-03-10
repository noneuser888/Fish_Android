package com.wantime.wbangapp.ui.activity

import androidx.fragment.app.Fragment
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseActivity
import com.wantime.wbangapp.ui.fragment.IPlatformFragment

class IPlatformApplyActivity : BaseActivity() {

    private var mIPlatformFragment: IPlatformFragment? = null

    override val layoutId: Int
        get() = R.layout.activity_fragment

    override fun afterInitView() {
        requestPermission()
        initFragment()
    }

    private fun initFragment() {
        mIPlatformFragment = IPlatformFragment.newInstance()
        if (mParamJson != null)
            mIPlatformFragment?.setParams(mParamJson!!)
        initFragment(mIPlatformFragment as Fragment, R.id.frameLayout)
    }
}