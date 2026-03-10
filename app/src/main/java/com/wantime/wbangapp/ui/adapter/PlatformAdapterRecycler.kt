package com.wantime.wbangapp.ui.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.wantime.wbangapp.R
import com.wantime.wbangapp.databinding.UiAdapterPlatformItemBinding
import com.wantime.wbangapp.databinding.UiAdapterPlatformRecyclerHeaderBinding
import com.wantime.wbangapp.inter.AdapterManager
import com.wantime.wbangapp.model.PlatformBean
import com.wantime.wbangapp.ui.event.IFormListener
import com.wantime.wbangapp.ui.widget.RoundableLayout


class PlatformAdapterRecycler(
    _context: Context,
    _mItemClick: RecyclerItemClick,
    _PagerClick: RecyclerItemClick,
    _IFormListener: IFormListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), AdapterManager {

    private val dataList = ArrayList<PlatformBean.PlatformListBean>()
    private val copyDataList = ArrayList<PlatformBean.PlatformListBean>()
    private var context: Context = _context
    private val mItemClick: RecyclerItemClick = _mItemClick
    private val mIFormListener: IFormListener = _IFormListener

    private val ITEM_TYPE_HEADER = 0
    private val ITEM_TYPE_CONTENT = 1
    private val ITEM_TYPE_BOTTOM = 2

    private var mHeaderCount = 0 //头部View个数
    private var mBottomCount = 1 //底部View个数

    private var mPageAdapter: PlatformPagerAdapter<PlatformBean.BannerBean> =
        PlatformPagerAdapter(_PagerClick)


    override fun onClear() {
        this.dataList.clear()
        this.copyDataList.clear()
    }

    fun pushDataList(dataList: ArrayList<PlatformBean.PlatformListBean>) {
        this.dataList.addAll(dataList)
        this.copyDataList.addAll(dataList)
    }

    fun pushBannerList(bannerList: ArrayList<PlatformBean.BannerBean>) {
        mPageAdapter.pushDataList(bannerList)
        mPageAdapter.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)

        val binding = DataBindingUtil.inflate(
            inflater,
            R.layout.ui_adapter_platform_item,
            parent,
            false
        ) as UiAdapterPlatformItemBinding
        return IViewHolder(binding)


//        return when (viewType) {
//            ITEM_TYPE_HEADER -> {
//                val binding = DataBindingUtil.inflate(
//                    inflater,
//                    R.layout.ui_adapter_platform_recycler_header,
//                    parent,
//                    false
//                ) as UiAdapterPlatformRecyclerHeaderBinding
//                HeaderViewHolder(binding)
//            }
//            else -> {
//                val binding = DataBindingUtil.inflate(
//                    inflater,
//                    R.layout.ui_adapter_platform_item,
//                    parent,
//                    false
//                ) as UiAdapterPlatformItemBinding
//                IViewHolder(binding)
//            }
//        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is IViewHolder) {
            holder.getBinding().iBean = dataList[position - mHeaderCount]
            holder.getBinding().executePendingBindings()
            holder.getBinding().xItemClick = mItemClick
            holder.getBinding().xFormClick = mIFormListener
        } else if (holder is HeaderViewHolder) {
            val mViewPager = holder.getBinding().root.findViewById<ViewPager>(R.id.mViewPager)
            if (mViewPager.adapter == null) {
                mViewPager.adapter = mPageAdapter
            }
            val mPager = holder.getBinding().root.findViewById<RoundableLayout>(R.id.layout_example)
            mPager.visibility = if (mPageAdapter.count > 0) View.VISIBLE else View.GONE
        }
    }

    override fun getItemCount(): Int {
        return dataList.size + mHeaderCount
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
    class IViewHolder(private val binding: UiAdapterPlatformItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun getBinding(): UiAdapterPlatformItemBinding {
            return binding
        }
    }

    //头部 ViewHolder
    class HeaderViewHolder(private val binding: UiAdapterPlatformRecyclerHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun getBinding(): UiAdapterPlatformRecyclerHeaderBinding {
            return binding
        }
    }

    fun onFilter(text: String) {
        if (!TextUtils.isEmpty(text)) {
            dataList.clear()
            for (item in copyDataList) {
                if ((item.nickname != null && item.nickname.contains(text))
                    ||(item.platformName!=null&&item.platformName.contains(text)))
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



