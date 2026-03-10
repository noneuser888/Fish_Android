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
import com.wantime.wbangapp.databinding.UiAdapterPersonalItemBinding
import com.wantime.wbangapp.inter.AdapterManager
import com.wantime.wbangapp.model.PersonalBean

class PersonAdapterRecycler(_context: Context, _mItemClick: RecyclerItemClick) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), AdapterManager {

    private val dataList = ArrayList<PersonalBean>()
    private var context: Context = _context
    private var mItemClick: RecyclerItemClick = _mItemClick

    override fun onClear() {
        this.dataList.clear()
    }

    fun pushData(_dataList: ArrayList<PersonalBean>) {
        this.dataList.addAll(_dataList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                val binding = DataBindingUtil.inflate(
                    LayoutInflater.from(context),
                    R.layout.ui_adapter_personal_item,
                    parent,
                    false
                ) as UiAdapterPersonalItemBinding
                PersonalItemViewHolder(binding)
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
        if (holder is PersonalItemViewHolder) {
            holder.getBinding().itemBean = dataList[position]
            holder.getBinding().executePendingBindings()
            holder.getBinding().itemClick = mItemClick

        } else if (holder is NullRowViewHolder) {
            holder.getBinding().executePendingBindings()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return dataList[position].type
    }

    //空行处理
    internal class NullRowViewHolder(private val binding: UiAdapterPernoalNullItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun getBinding(): UiAdapterPernoalNullItemBinding {
            return binding
        }
    }

    //数据行处理
    internal class PersonalItemViewHolder(private val binding: UiAdapterPersonalItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun getBinding(): UiAdapterPersonalItemBinding {
            return binding
        }
    }

}