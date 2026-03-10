package com.wantime.wbangapp.ui.fragment

import android.view.View
import android.widget.Button
import androidx.lifecycle.Observer
import com.alibaba.fastjson.JSON
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseFragment
import com.wantime.wbangapp.databinding.FragmentSingleRecordBinding
import com.wantime.wbangapp.model.LoginAuthorityBean
import com.wantime.wbangapp.ui.adapter.RecyclerItemClick
import com.wantime.wbangapp.ui.adapter.SingleRecordAdapterRecycler
import com.wantime.wbangapp.ui.event.MessageEvent
import com.wantime.wbangapp.ui.widget.SearchView
import com.wantime.wbangapp.viewmodel.AuthorityRecordViewModel
import kotlinx.android.synthetic.main.activity_upgrade.*
import kotlinx.android.synthetic.main.fragment_platform.*
import kotlinx.android.synthetic.main.fragment_single_record.*
import kotlinx.android.synthetic.main.fragment_single_record.mRecyclerView
import kotlinx.android.synthetic.main.fragment_single_record.refreshLayout
import kotlinx.android.synthetic.main.fragment_single_record.searchView
import org.greenrobot.eventbus.EventBus

class SingleRecordFragment : BaseFragment<AuthorityRecordViewModel, FragmentSingleRecordBinding>() {
    private var mAdapter: SingleRecordAdapterRecycler? = null
    private var appId: String = ""
    private var mFirstBean: LoginAuthorityBean.ListBean.RecordsBean? = null
    private var timeArray = arrayOf("", "")

    override val layoutResource: Int
        get() = R.layout.fragment_single_record

    override fun afterInitView() {
        mAdapter = SingleRecordAdapterRecycler(activity!!, object : RecyclerItemClick {
            override fun onItemClick(itemView: View, iBean: Any) {
                this@SingleRecordFragment.onItemClick(itemView, iBean)
            }
        }, object : RecyclerItemClick {
            override fun onItemClick(itemView: View, iBean: Any) {
                mFirstBean = null
                mAdapter?.pushHeadData(mFirstBean)
                mAdapter?.notifyDataSetChanged()
            }
        })
        mRecyclerView.adapter = mAdapter
        initSearchView()
        onInitRefresh()
    }

    private fun initSearchView() {
        searchView.addSearchViewListener(object : SearchView.SearchViewListener {
            override fun onSearch(mText: String) {
                mAdapter?.onFilter(mText)
            }
        })
    }

    override fun onProcessLogic() {
        if (mParamJson != null) {
            appId = mParamJson!!.optString("appId", "")
            refreshLayout.autoRefresh()
        }
    }


    private fun onInitRefresh() {
        refreshLayout.setOnRefreshListener {
            loadRefreshData()
        }
        refreshLayout.setOnLoadMoreListener {
            loadMoreData()
        }
    }


    private fun loadRefreshData() {
        mViewModel?.onLoginAuthorityRefreshNews(appId, timeArray[0], timeArray[1])
            ?.observe(this, Observer {
                mRecyclerView.post {
                    if(it.list!=null&&it.list.records!=null) {
                        mAdapter?.onClear()
                        mAdapter?.pushData(it.list.records as ArrayList<LoginAuthorityBean.ListBean.RecordsBean>)
                        if (mFirstBean != null) mAdapter?.pushHeadData(mFirstBean!!)
                        mAdapter?.notifyDataSetChanged()
                    }
                    refreshLayout.finishRefresh()
                }
            })
    }

    private fun loadMoreData() {
        mViewModel?.onLoginAuthorityLoadMore(appId, timeArray[0], timeArray[1])
            ?.observe(this, Observer {
                mRecyclerView.post {
                    if(it.list!=null&&it.list.records!=null) {
                        mAdapter?.pushData(it.list.records as ArrayList<LoginAuthorityBean.ListBean.RecordsBean>)
                        mAdapter?.notifyDataSetChanged()
                    }
                    refreshLayout.finishLoadMore()
                }
            })
    }

    private fun onItemClick(itemView: View, iBean: Any) {

        when (itemView) {
            is Button -> {
                mFirstBean = iBean as LoginAuthorityBean.ListBean.RecordsBean
                if (mFirstBean != null) {//传回数据到扫描界面
                    if (mFirstBean?.type != 0) {
                        //mAdapter?.pushHeadData(mFirstBean!!)
                        val messageEvent = MessageEvent()
                        messageEvent.mType = MessageEvent.EventType.reaptScan.ordinal
                        messageEvent.dataJson = JSON.toJSONString(mFirstBean)
                        EventBus.getDefault().post(messageEvent)
                        activity?.finish()
                    }
                    mFirstBean = null
                }
            }
        }
        //mAdapter?.notifyDataSetChanged()
    }


    fun onSetFilterTime(startTime: String, endTime: String) {
        timeArray[0] = startTime
        timeArray[1] = endTime
        refreshLayout.autoRefresh()
    }
}