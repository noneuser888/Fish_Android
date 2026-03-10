package com.wantime.wbangapp.ui.fragment

import android.view.View
import androidx.lifecycle.Observer
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseFragment
import com.wantime.wbangapp.databinding.FragmentShensuBinding
import com.wantime.wbangapp.model.AppealRecordBean
import com.wantime.wbangapp.ui.adapter.AppealAdapterRecycler
import com.wantime.wbangapp.ui.adapter.RecyclerItemClick
import com.wantime.wbangapp.viewmodel.AppealViewModel
import kotlinx.android.synthetic.main.fragment_shensu.*
import kotlinx.android.synthetic.main.fragment_shensu.refreshLayout
import kotlinx.android.synthetic.main.ui_top_navigation.*

//申诉记录
class AppealRecordFragment : BaseFragment<AppealViewModel, FragmentShensuBinding>() {

    private var mAdapter: AppealAdapterRecycler? = null

    companion object {
        private var mAppealFragment: AppealRecordFragment? = null
        fun newInstance(): AppealRecordFragment {
            if (mAppealFragment == null) {
                synchronized(AppealRecordFragment::class.java) {
                    mAppealFragment =
                        AppealRecordFragment()
                }
            }
            return mAppealFragment!!
        }
    }


    override val layoutResource: Int
        get() = R.layout.fragment_shensu

    override fun afterInitView() {
        navBack.visibility = View.VISIBLE
        navBack.setOnClickListener { activity!!.finish() }
        navTitle.text = getString(R.string.ui_shensu_record)
        onInitRefresh()
        initRecyclerView()
    }


    private fun onInitRefresh() {
        refreshLayout.setOnRefreshListener {
            mViewModel?.onRefreshNews()?.observe(this, Observer {
                onInitRefresh(it)
            })
        }
        refreshLayout.setOnLoadMoreListener {
            mViewModel?.onLoadMore()?.observe(this, Observer {
                onLoadMore(it)
            })
        }
    }

    override fun onProcessLogic() {
        mViewModel?.onRefreshNews()?.observe(this, Observer {
            onInitRefresh(it)
        })
    }

    private fun initRecyclerView() {
        mAdapter = AppealAdapterRecycler(activity!!, object : RecyclerItemClick {
            override fun onItemClick(itemView: View, iBean: Any) {
                this@AppealRecordFragment.onItemClick(itemView, iBean)
            }
        })

        mRecyclerView.adapter = mAdapter
    }

    private fun onInitRefresh(iBean: AppealRecordBean) {
        mRecyclerView.post {
            if (iBean.records != null) {
                mAdapter!!.onClear()
                mAdapter!!.pushData(iBean.records as ArrayList<AppealRecordBean.RecordsBean>)
                mAdapter!!.notifyDataSetChanged()
            }
            refreshLayout.finishRefresh()
        }
    }

    private fun onLoadMore(iBean: AppealRecordBean) {
        mRecyclerView.post {
            mAdapter!!.pushData(iBean.records as ArrayList<AppealRecordBean.RecordsBean>)
            mAdapter!!.notifyDataSetChanged()
            refreshLayout.finishLoadMore()
        }
    }


    private fun onItemClick(itemView: View, iBean: Any) {

    }
}