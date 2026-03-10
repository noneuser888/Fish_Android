package com.wantime.wbangapp.ui.activity

import androidx.fragment.app.Fragment
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseActivity
import com.wantime.wbangapp.ui.fragment.LoginFragment

class LoginActivity : BaseActivity(){
    private var mLoginFragment: LoginFragment?=null

    override val layoutId: Int
        get() = R.layout.activity_launch

    override fun afterInitView() {
        requestPermission()
        initFragment()
    }

    private fun initFragment(){
        mLoginFragment= LoginFragment()
        initFragment(mLoginFragment as Fragment, R.id.frameLayout)
    }
}