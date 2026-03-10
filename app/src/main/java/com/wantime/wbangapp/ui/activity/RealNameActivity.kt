package com.wantime.wbangapp.ui.activity

import android.view.View
import androidx.fragment.app.Fragment
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseActivity
import com.wantime.wbangapp.ui.fragment.RealNameFragment
import kotlinx.android.synthetic.main.ui_top_navigation.*

/***实名制认证***/
class RealNameActivity : BaseActivity() {

    private var mRealNameFragment: RealNameFragment? = null

    override val layoutId: Int
        get() = R.layout.activity_fragment_with_record

    override fun afterInitView() {
        navBack.visibility= View.VISIBLE
        navBack.setOnClickListener { finish() }
        navTitle.text="实名制认证"
        initFragment()
    }

    private fun initFragment() {
        mRealNameFragment = RealNameFragment()
        if (mParamJson != null)
            mRealNameFragment?.setParams(mParamJson!!)
        initFragment(mRealNameFragment as Fragment, R.id.frameLayout)
    }
}