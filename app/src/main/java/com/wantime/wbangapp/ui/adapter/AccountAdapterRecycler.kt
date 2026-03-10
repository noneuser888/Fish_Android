package com.wantime.wbangapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wantime.wbangapp.R
import com.wantime.wbangapp.databinding.NewUiMenuAccountRecylerItemBinding
import com.wantime.wbangapp.inter.AdapterManager
import com.wantime.wbangapp.model.AccountBean


class AccountAdapterRecycler(
    _context: Context,
    _mItemClick: RecyclerItemClick
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), AdapterManager {

    private val dataList = ArrayList<AccountBean>()
    private var context: Context = _context
    private var mItemClick: RecyclerItemClick = _mItemClick

    private val ITEM_TYPE_HEADER = 0
    private val ITEM_TYPE_CONTENT = 1
    private val ITEM_TYPE_BOTTOM = 2

    private var mHeaderCount = 0 //头部View个数
    private var mBottomCount = 1 //底部View个数
    private var adTitle = ""


    //判断当前item是否是HeadView
    fun isHeaderView(position: Int): Boolean {
        return mHeaderCount != 0 && position < mHeaderCount
    }

    //判断当前item是否是FooterView
    fun isBottomView(position: Int): Boolean {
        return mBottomCount != 0 && position >= mHeaderCount + itemCount
    }

    fun isShowHeadView(isShow: Boolean): AccountAdapterRecycler {
        mHeaderCount = if (isShow) 1 else 0
        return this
    }

    override fun onClear() {
        dataList.clear()
    }

    fun pushDataList(dataList: ArrayList<AccountBean>) {
        this.dataList.addAll(dataList)
    }


    fun setAdTitle(adTitle: String) {
        this.adTitle = adTitle
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = DataBindingUtil.inflate(
            inflater,
            R.layout.new_ui_menu_account_recyler_item,
            parent,
            false
        ) as NewUiMenuAccountRecylerItemBinding
     return    IViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is IViewHolder) {
            holder.getBinding().accountBean = dataList[position-mHeaderCount]
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
    class IViewHolder(private val binding: NewUiMenuAccountRecylerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun getBinding(): NewUiMenuAccountRecylerItemBinding {
            return binding
        }
    }

}