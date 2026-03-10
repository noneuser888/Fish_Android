package com.wantime.wbangapp.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wantime.wbangapp.R
import com.wantime.wbangapp.databinding.UiAdapterPernoalNullItemBinding
import com.wantime.wbangapp.databinding.UiAdapterZsRecordItemBinding
import com.wantime.wbangapp.inter.AdapterManager
import com.wantime.wbangapp.model.LoginAuthorityBean
import com.wantime.wbangapp.ui.event.IFormListener
import com.wantime.wbangapp.utils.Constants
import com.wantime.wbangapp.utils.GlideImageLoader

//专属授权记录的适配器
class AuthorityZSRecordAdapterRecycler(
    _context: Context,
    _mItemClick: RecyclerItemClick,
    _mFormClick: IFormListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), AdapterManager {

    private val dataList = ArrayList<LoginAuthorityBean.ListBean.RecordsBean>()
    private var context: Context = _context
    private var mItemClick: RecyclerItemClick = _mItemClick
    private var _mFormClick: IFormListener = _mFormClick

    fun pushData(_dataList: ArrayList<LoginAuthorityBean.ListBean.RecordsBean>) {
        this.dataList.addAll(_dataList)
    }

    override fun onClear() {
        this.dataList.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                val binding = DataBindingUtil.inflate(
                    LayoutInflater.from(context),
                    R.layout.ui_adapter_pernoal_null_item,
                    parent,
                    false
                ) as UiAdapterPernoalNullItemBinding
                NullRowViewHolder(binding)
            }
            else -> {
                val binding = DataBindingUtil.inflate(
                    LayoutInflater.from(context),
                    R.layout.ui_adapter_zs_record_item,
                    parent,
                    false
                ) as UiAdapterZsRecordItemBinding
                RecordItemViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RecordItemViewHolder) {
            holder.getBinding().itemBean = dataList[position]
            val recycleLinear =
                holder.getBinding().root.findViewById<LinearLayout>(R.id.recycleLinear)
            holder.getBinding().executePendingBindings()
            onRefreshImgList(recycleLinear, dataList[position].imgList)
            holder.getBinding().itemClick = mItemClick
            holder.getBinding().iFormClick = _mFormClick
        } else if (holder is NullRowViewHolder) {
            holder.getBinding().executePendingBindings()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return dataList[position].type
    }

    //刷新imageList
    private fun onRefreshImgList(linearLayout: LinearLayout, imgList: List<String>?) {
        linearLayout.visibility =
            if (imgList == null || imgList.isEmpty()) View.GONE else View.VISIBLE
        val inflater = LayoutInflater.from(context)
        if (imgList != null && imgList.isNotEmpty()) {
            linearLayout.removeAllViews()
            for (item in imgList) {
                val itemImg =
                    inflater.inflate(R.layout.ui_record_linear_item_image, null) as ImageView
                GlideImageLoader.getInstance().displayImage(context, item, itemImg)
                linearLayout.addView(itemImg)
                (itemImg.layoutParams as LinearLayout.LayoutParams).rightMargin =
                    Constants.dip2px(context, 5f)
            }

        }
    }

    //空行处理
    internal class NullRowViewHolder(private val binding: UiAdapterPernoalNullItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun getBinding(): UiAdapterPernoalNullItemBinding {
            return binding
        }
    }

    //数据行处理
    internal class RecordItemViewHolder(private val binding: UiAdapterZsRecordItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun getBinding(): UiAdapterZsRecordItemBinding {
            return binding
        }
    }

}