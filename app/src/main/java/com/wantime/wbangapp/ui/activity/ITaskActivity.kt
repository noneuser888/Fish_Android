package com.wantime.wbangapp.ui.activity

import androidx.fragment.app.Fragment
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseActivity
import com.wantime.wbangapp.ui.fragment.ITaskFragment

class ITaskActivity :BaseActivity() {

    private var mITaskFragment: ITaskFragment?=null

    override val layoutId: Int
        get() = R.layout.activity_fragment

    override fun afterInitView() {
        requestPermission()
        initFragment()
    }

    private fun initFragment(){
        mITaskFragment= ITaskFragment.newInstance()
        initFragment(mITaskFragment as Fragment, R.id.frameLayout)
    }
}