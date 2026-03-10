package com.wantime.wbangapp.ui.activity

import androidx.fragment.app.Fragment
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseActivity
import com.wantime.wbangapp.ui.fragment.UserUpgradeFragment
import kotlin.properties.Delegates

class UserUpgradeActivity : BaseActivity() {

    private var mUserUpgradeFragment: UserUpgradeFragment by Delegates.notNull()
    override val layoutId: Int
        get() = R.layout.activity_fragment

    override fun afterInitView() {
        initFragment()
    }

    private fun initFragment() {
        mUserUpgradeFragment = UserUpgradeFragment()
        if (mParamJson != null)
            mUserUpgradeFragment.setParams(mParamJson!!)
        initFragment(mUserUpgradeFragment as Fragment, R.id.frameLayout)
    }

}