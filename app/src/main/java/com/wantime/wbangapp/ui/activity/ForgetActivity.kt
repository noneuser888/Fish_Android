package com.wantime.wbangapp.ui.activity

import androidx.fragment.app.Fragment
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseActivity
import com.wantime.wbangapp.ui.fragment.ForgetFragment

//忘记密码
class ForgetActivity : BaseActivity() {

    private var mForgetFragment: ForgetFragment? = null

    override val layoutId: Int
        get() = R.layout.activity_fragment

    override fun afterInitView() {
        initFragment()
    }

    private fun initFragment() {
        mForgetFragment = ForgetFragment()
        mForgetFragment?.setViewType(viewType)
        if (mParamJson != null)
            mForgetFragment?.setParams(mParamJson!!)
        initFragment(mForgetFragment as Fragment, R.id.frameLayout)
    }
}