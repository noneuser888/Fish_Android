package com.wantime.wbangapp.ui.fragment

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseFragment
import com.wantime.wbangapp.databinding.FragmentRechargeRecordBinding
import com.wantime.wbangapp.model.RechargeRecordBean
import com.wantime.wbangapp.ui.adapter.RechargeRecordRecycler
import com.wantime.wbangapp.ui.adapter.RecyclerItemClick
import com.wantime.wbangapp.utils.Constants
import com.wantime.wbangapp.viewmodel.RechargeRecordViewModel
import kotlinx.android.synthetic.main.fragment_recharge_record.*
import kotlin.properties.Delegates

class RechargeRecordFragment :
    BaseFragment<RechargeRecordViewModel, FragmentRechargeRecordBinding>() {

    private var mAdapter: RechargeRecordRecycler by Delegates.notNull()

    override val layoutResource: Int
        get() = R.layout.fragment_recharge_record

    override fun afterInitView() {
        initRecyclerView()
    }

    override fun onProcessLogic() {
        onRefreshList()
    }

    private fun onRefreshList() {
        mViewModel?.onRefreshRechargeRecord()?.observe(this, Observer {
            if (it.records != null) {
                mAdapter.onClear()
                mAdapter.pushDataList(it.records as ArrayList<RechargeRecordBean.RecordsBean>)
                mAdapter.notifyDataSetChanged()
                refreshLayout.finishRefresh()
            }
        })
    }

    private fun onLoadMoreList(){
        mViewModel?.onLoadMoreRechargeRecord()?.observe(this, Observer {
            if (it.records != null) {
                mAdapter.pushDataList(it.records as ArrayList<RechargeRecordBean.RecordsBean>)
                mAdapter.notifyDataSetChanged()
                refreshLayout.finishLoadMore()
            }
        })
    }


    private fun initRecyclerView() {
        binding?.rechargeRecordHead?.personInfoLayout?.xUserBean =
            Constants.onGetUserBaseInfoWithNoToken()
        //添加自定义分割线
        val divider = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(activity!!, R.drawable.custom_divider)!!)
        mRecyclerView.addItemDecoration(divider)
        //设置适配器
        mAdapter = RechargeRecordRecycler(activity!!, object : RecyclerItemClick {
            override fun onItemClick(itemView: View, iBean: Any) {
                this@RechargeRecordFragment.onItemClick(itemView, iBean)
            }
        }, object : RecyclerItemClick {
            override fun onItemClick(itemView: View, iBean: Any) {
                this@RechargeRecordFragment.onPagerItemClick(itemView, iBean)
            }
        })
        mRecyclerView.adapter = mAdapter
        initUserInfo()
    }

    private fun initUserInfo() {
        binding?.rechargeRecordHead?.mNavigationBar?.findViewById<TextView>(R.id.navTitle)?.text =
            getString(R.string.new_ui_recharge_record)
        binding?.rechargeRecordHead?.mNavigationBar!!.findViewById<TextView>(R.id.navBack)
            .setOnClickListener {
                activity!!.finish()
            }
        refreshLayout.setOnLoadMoreListener {
            onLoadMoreList()
        }
        refreshLayout.setOnRefreshListener {onRefreshList()  }
    }

    private fun onPagerItemClick(itemView: View, iBean: Any) {

    }

    private fun onItemClick(itemView: View, iBean: Any) {

    }
}