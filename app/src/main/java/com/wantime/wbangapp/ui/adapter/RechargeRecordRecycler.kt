package com.wantime.wbangapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wantime.wbangapp.R
import com.wantime.wbangapp.databinding.NewUiAgentManagerAdapterHeadBinding
import com.wantime.wbangapp.databinding.NewUiMainAdapterHeadBinding
import com.wantime.wbangapp.databinding.NewUiRechargeAdapterItemBinding
import com.wantime.wbangapp.databinding.UiAdapterAuthorityItemBinding
import com.wantime.wbangapp.inter.AdapterManager
import com.wantime.wbangapp.model.NewsBean
import com.wantime.wbangapp.model.RechargeRecordBean
import com.wantime.wbangapp.utils.Constants


class RechargeRecordRecycler(
    _context: Context,
    _mItemClick: RecyclerItemClick,
    _PagerClick: RecyclerItemClick
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), AdapterManager {

    private val dataList = ArrayList<RechargeRecordBean.RecordsBean>()
    private var context: Context = _context
    private var mItemClick: RecyclerItemClick = _mItemClick

    private val ITEM_TYPE_HEADER = 0
    private val ITEM_TYPE_CONTENT = 1
    private val ITEM_TYPE_BOTTOM = 2

    private var mHeaderCount = 0 //头部View个数
    private var mBottomCount = 1 //底部View个数
    private var adTitle = ""

    private var mPageAdapter: AuthorityPagerAdapter<RechargeRecordBean.RecordsBean> =
        AuthorityPagerAdapter(_PagerClick)

    //判断当前item是否是HeadView
    fun isHeaderView(position: Int): Boolean {
        return mHeaderCount != 0 && position < mHeaderCount
    }

    //判断当前item是否是FooterView
    fun isBottomView(position: Int): Boolean {
        return mBottomCount != 0 && position >= mHeaderCount + itemCount
    }

    fun isShowHeadView(isShow: Boolean): RechargeRecordRecycler {
        mHeaderCount = if (isShow) 1 else 0
        return this
    }

    override fun onClear() {
        dataList.clear()
    }

    fun pushDataList(dataList: ArrayList<RechargeRecordBean.RecordsBean>) {
        this.dataList.addAll(dataList)
    }

//    fun pushBannerList(bannerList: ArrayList<RechargeRecordBean.RecordsBean>) {
//        mPageAdapter.pushDataList(bannerList)
//        mPageAdapter.notifyDataSetChanged()
//    }

    fun setAdTitle(adTitle: String) {
        this.adTitle = adTitle
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = DataBindingUtil.inflate(
            inflater,
            R.layout.new_ui_recharge_adapter_item,
            parent,
            false
        ) as NewUiRechargeAdapterItemBinding
     return    IViewHolder(binding)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is IViewHolder) {
            holder.getBinding().xNewsBean = dataList[position-mHeaderCount]
            holder.getBinding().executePendingBindings()
            holder.getBinding().xItemClick = mItemClick
        }
    }

    override fun getItemCount(): Int {
        return dataList.size+mHeaderCount
    }

    override fun getItemViewType(position: Int): Int {
        val dataItemCount: Int = itemCount
        return if (mHeaderCount != 0 && position < mHeaderCount) {
            //头部View
            ITEM_TYPE_HEADER
        } else if (mBottomCount != 0 && position >= mHeaderCount + dataItemCount) {
            //底部View
            ITEM_TYPE_BOTTOM
        } else {
            //内容View
            ITEM_TYPE_CONTENT
        }
    }

    //内容
    class IViewHolder(private val binding: NewUiRechargeAdapterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun getBinding(): NewUiRechargeAdapterItemBinding {
            return binding
        }
    }
//
//    //头部 ViewHolder
//    class HeaderViewHolder(private val binding: NewUiAgentManagerAdapterHeadBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//        fun getBinding(): NewUiAgentManagerAdapterHeadBinding {
//            return binding
//        }
//    }
}