package com.wantime.wbangapp.ui.activity

import android.view.View
import androidx.fragment.app.Fragment
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseActivity
import com.wantime.wbangapp.ui.fragment.SingleRecordFragment
import com.wantime.wbangapp.utils.Constants
import dateselecter.chs.com.dateselecter.DateTimePicker
import kotlinx.android.synthetic.main.activity_fragment_with_record.*
import kotlinx.android.synthetic.main.ui_top_navigation.*

//单个授权记录
class SingleRecordActivity : BaseActivity() {

    private var mSingleRecordFragment: SingleRecordFragment? = null
    private var mDateTimePicker: DateTimePicker = DateTimePicker()
    override val layoutId: Int
        get() = R.layout.activity_fragment_with_record

    override fun afterInitView() {
        initFragment()
    }

    private fun initFragment() {
        navBack.visibility = View.VISIBLE
        navRightIcon.visibility = View.VISIBLE
        navRightIcon.text = "筛选"
        navBack.setOnClickListener { finish() }
        navRightIcon.setOnClickListener { mDateTimePicker.showPopView() }
        mSingleRecordFragment = SingleRecordFragment()
        if (mParamJson != null) {
            mSingleRecordFragment?.setParams(mParamJson!!)
            navTitle.text = mParamJson!!.optString("nickname")
        }
        initFragment(mSingleRecordFragment as Fragment, R.id.frameLayout)
        mDateTimePicker.initDateTimePicker(this, mLayoutView)
        mDateTimePicker.addDateTimePickerResultListener {
            mSingleRecordFragment?.onSetFilterTime(
                Constants.onGetTimeStamp(it[0]).toString(),
                Constants.onGetTimeStamp(it[1]).toString()
            )
        }
    }
}