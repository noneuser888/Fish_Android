package com.wantime.wbangapp.ui.activity

import androidx.fragment.app.Fragment
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseActivity
import com.wantime.wbangapp.ui.fragment.PlatformFragment
import kotlin.properties.Delegates

class PlatformListActivity : BaseActivity() {

    private var mIPlatformFragment: PlatformFragment by Delegates.notNull()

    override val layoutId: Int
        get() = R.layout.activity_fragment

    override fun afterInitView() {
        requestPermission()
        initFragment()
    }

    private fun initFragment() {
        mIPlatformFragment = PlatformFragment()
        if (mParamJson != null)
            mIPlatformFragment.setParams(mParamJson!!)
        initFragment(mIPlatformFragment as Fragment, R.id.frameLayout)
    }
}