package com.wantime.wbangapp.ui.fragment

import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseFragment
import com.wantime.wbangapp.databinding.FragmentRepresentationsBinding
import com.wantime.wbangapp.viewmodel.RepresentationsViewModel

class RepresentationsRecordFragment : BaseFragment<RepresentationsViewModel, FragmentRepresentationsBinding>() {

    companion object {
        private var mRepresentationsRecordFragment: RepresentationsRecordFragment? = null
        fun newInstance(): RepresentationsRecordFragment {
            if (mRepresentationsRecordFragment == null) {
                synchronized(RepresentationsRecordFragment::class.java) {
                    mRepresentationsRecordFragment =
                        RepresentationsRecordFragment()
                }
            }
            return mRepresentationsRecordFragment!!
        }
    }

    override val layoutResource: Int
        get() = R.layout.fragment_representations

    override fun afterInitView() {

    }

    override fun onProcessLogic() {

    }
}