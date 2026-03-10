package com.wantime.wbangapp.ui.activity

import android.view.View
import androidx.fragment.app.Fragment
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseActivity
import com.wantime.wbangapp.ui.fragment.RegisterFragment
import kotlinx.android.synthetic.main.ui_top_navigation.*

/***用户注册***/
class RegisterActivity : BaseActivity() {

    private var mRegisterFragment: RegisterFragment? = null

    override val layoutId: Int
        get() = R.layout.activity_fragment_with_nav

    override fun afterInitView() {
        navBack.visibility = View.VISIBLE
        navBack.setOnClickListener { finish() }
        navTitle.text=getString(R.string.ui_title_register)
        initFragment()
    }

    private fun initFragment() {
        mRegisterFragment = RegisterFragment()
        if (mParamJson != null)
            mRegisterFragment?.setParams(mParamJson!!)
        initFragment(mRegisterFragment as Fragment, R.id.frameLayout)
    }
}