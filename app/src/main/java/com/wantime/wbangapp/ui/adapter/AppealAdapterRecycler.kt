package com.wantime.wbangapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wantime.wbangapp.R
import com.wantime.wbangapp.databinding.UiAdapterPernoalNullItemBinding
import com.wantime.wbangapp.databinding.UiAdapterRecordShensuItemBinding
import com.wantime.wbangapp.inter.AdapterManager
import com.wantime.wbangapp.model.AppealRecordBean

class AppealAdapterRecycler(_context: Context, _mItemClick: RecyclerItemClick) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), AdapterManager {

    private val dataList = ArrayList<AppealRecordBean.RecordsBean>()
    private var context: Context = _context
    private var mItemClick: RecyclerItemClick = _mItemClick

    override fun onClear() {
        dataList.clear()
    }

    fun pushData(_dataList: ArrayList<AppealRecordBean.RecordsBean>) {
        this.dataList.addAll(_dataList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                val binding = DataBindingUtil.inflate(
                    LayoutInflater.from(context),
                    R.layout.ui_adapter_record_shensu_item,
                    parent,
                    false
                ) as UiAdapterRecordShensuItemBinding
                AppealItemViewHolder(binding)
            }
            else -> {
                val binding = DataBindingUtil.inflate(
                    LayoutInflater.from(context),
                    R.layout.ui_adapter_pernoal_null_item,
                    parent,
                    false
                ) as UiAdapterPernoalNullItemBinding
                NullRowViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AppealItemViewHolder) {
            holder.getBinding().itemBean = dataList[position]
            holder.getBinding().executePendingBindings()
            holder.getBinding().itemClick = mItemClick

        } else if (holder is NullRowViewHolder) {
            holder.getBinding().executePendingBindings()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    //空行处理
    internal class NullRowViewHolder(private val binding: UiAdapterPernoalNullItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun getBinding(): UiAdapterPernoalNullItemBinding {
            return binding
        }
    }

    //数据行处理
    internal class AppealItemViewHolder(private val binding: UiAdapterRecordShensuItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun getBinding(): UiAdapterRecordShensuItemBinding {
            return binding
        }
    }

}