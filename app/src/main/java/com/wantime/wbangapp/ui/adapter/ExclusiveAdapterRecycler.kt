package com.wantime.wbangapp.ui.adapter

import android.content.Context
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wantime.wbangapp.R
import com.wantime.wbangapp.databinding.UiAdapterAuthorityItemBinding
import com.wantime.wbangapp.databinding.UiAdapterExclusiveHeadBinding
import com.wantime.wbangapp.databinding.UiAdapterExclusivePlatformItemBinding
import com.wantime.wbangapp.databinding.UiAuthorityRecyclerHeadBinding
import com.wantime.wbangapp.inter.AdapterManager
import com.wantime.wbangapp.model.ExclusiveBean
import com.wantime.wbangapp.model.NewsBean
import com.wantime.wbangapp.ui.event.IFormListener


class ExclusiveAdapterRecycler(
    _context: Context,
    _mItemClick: RecyclerItemClick,
    _IFormListener: IFormListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), AdapterManager {

    private val dataList = ArrayList<ExclusiveBean.ListBean>()
    private val copyDataList = ArrayList<ExclusiveBean.ListBean>()
    private var context: Context = _context
    private var mItemClick: RecyclerItemClick = _mItemClick
    private val mIFormListener: IFormListener = _IFormListener

    private val ITEM_TYPE_HEADER = 0
    private val ITEM_TYPE_CONTENT = 1
    private val ITEM_TYPE_BOTTOM = 2

    private var mHeaderCount = 1 //头部View个数
    private var mBottomCount = 1 //底部View个数
    private var mLineNumber = 0


    override fun onClear() {
        dataList.clear()
        copyDataList.clear()
    }

    fun pushDataList(dataList: ArrayList<ExclusiveBean.ListBean>) {
        this.dataList.addAll(dataList)
        this.copyDataList.addAll(dataList)
    }

    fun setOnlineNumber(number: Int) {
        this.mLineNumber = number
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        return when (viewType) {
            ITEM_TYPE_HEADER -> {
                val binding = DataBindingUtil.inflate(
                    inflater,
                    R.layout.ui_adapter_exclusive_head,
                    parent,
                    false
                ) as UiAdapterExclusiveHeadBinding
                HeaderViewHolder(binding)
            }
            else -> {
                val binding = DataBindingUtil.inflate(
                    inflater,
                    R.layout.ui_adapter_exclusive_platform_item,
                    parent,
                    false
                ) as UiAdapterExclusivePlatformItemBinding
                IViewHolder(binding)
            }
        }


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is IViewHolder) {
            holder.getBinding().iBean = dataList[position-mHeaderCount]
            holder.getBinding().executePendingBindings()
            holder.getBinding().xItemClick = mItemClick
            holder.getBinding().xFormClick = mIFormListener
        } else if (holder is HeaderViewHolder) {
            holder.getBinding().root.findViewById<TextView>(R.id.headLine).text =
                context.resources.getString(R.string.ui_online_number) + "$mLineNumber"
//            holder.getBinding().root.findViewById<TextView>(R.id.adText).text =context.getString(R.string.platformlisttip1)

        }
    }

    override fun getItemCount(): Int {
        return dataList.size
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
    class IViewHolder(private val binding: UiAdapterExclusivePlatformItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun getBinding(): UiAdapterExclusivePlatformItemBinding {
            return binding
        }
    }

    //头部 ViewHolder
    class HeaderViewHolder(private val binding: UiAdapterExclusiveHeadBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun getBinding(): UiAdapterExclusiveHeadBinding {
            return binding
        }
    }


    fun onFilter(text: String) {
        if (!TextUtils.isEmpty(text)) {
            dataList.clear()
            for (item in copyDataList) {
                if (item.nickname != null && item.nickname.contains(text))
                    dataList.add(item)
            }
            notifyDataSetChanged()
        } else {
            dataList.clear()
            dataList.addAll(copyDataList)
            notifyDataSetChanged()
        }
    }
}