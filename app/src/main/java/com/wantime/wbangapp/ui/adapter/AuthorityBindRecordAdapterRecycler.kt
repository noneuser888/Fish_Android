package com.wantime.wbangapp.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wantime.wbangapp.R
import com.wantime.wbangapp.databinding.UiAdapterPernoalNullItemBinding
import com.wantime.wbangapp.databinding.UiAdapterRecordItemBinding
import com.wantime.wbangapp.inter.AdapterManager
import com.wantime.wbangapp.model.LoginAuthorityBean
//代绑授权记录的适配器
class AuthorityBindRecordAdapterRecycler(_context: Context, _mItemClick: RecyclerItemClick) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),AdapterManager {

    private val dataList = ArrayList<LoginAuthorityBean.ListBean.RecordsBean>()
    private var context: Context = _context
    private var mItemClick: RecyclerItemClick = _mItemClick

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
                    R.layout.ui_adapter_record_item,
                    parent,
                    false
                ) as UiAdapterRecordItemBinding
                RecordItemViewHolder(binding)
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
        if (holder is RecordItemViewHolder) {
            holder.getBinding().itemBean = dataList[position]
            holder.getBinding().executePendingBindings()
            holder.getBinding().itemClick=mItemClick
        } else if (holder is NullRowViewHolder) {
            holder.getBinding().executePendingBindings()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return dataList[position].type
    }

    //空行处理
    internal class NullRowViewHolder(private val binding: UiAdapterPernoalNullItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun getBinding(): UiAdapterPernoalNullItemBinding {
            return binding
        }
    }

    //数据行处理
    internal class RecordItemViewHolder(private val binding: UiAdapterRecordItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun getBinding(): UiAdapterRecordItemBinding {
            return binding
        }
    }

}