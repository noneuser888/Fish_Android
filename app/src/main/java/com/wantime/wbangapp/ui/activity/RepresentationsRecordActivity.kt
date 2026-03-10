package com.wantime.wbangapp.ui.activity

import androidx.fragment.app.Fragment
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseActivity
import com.wantime.wbangapp.ui.fragment.RepresentationsRecordFragment

class RepresentationsRecordActivity :BaseActivity () {

    private var mRepresentationsRecordFragment: RepresentationsRecordFragment?=null

    override val layoutId: Int
        get() = R.layout.activity_fragment

    override fun afterInitView() {
        requestPermission()
        initFragment()
    }

    private fun initFragment(){
        mRepresentationsRecordFragment= RepresentationsRecordFragment.newInstance()
        initFragment(mRepresentationsRecordFragment as Fragment, R.id.frameLayout)
    }
}