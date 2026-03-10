package com.wantime.wbangapp.ui.activity

import androidx.fragment.app.Fragment
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseActivity
import com.wantime.wbangapp.ui.fragment.AuthorityRecordFragment

//授权记录
class RecordActivity : BaseActivity() {

    private var mRecordFragment: AuthorityRecordFragment? = null

    override val layoutId: Int
        get() = R.layout.activity_fragment

    override fun afterInitView() {
        initFragment()
    }

    private fun initFragment() {
        mRecordFragment = AuthorityRecordFragment()
        mRecordFragment?.setViewType(viewType)
        if (mParamJson != null)
            mRecordFragment?.setParams(mParamJson!!)
        initFragment(mRecordFragment as Fragment, R.id.frameLayout)
    }
}