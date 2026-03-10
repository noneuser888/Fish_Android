package com.wantime.wbangapp.ui.adapter

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wantime.wbangapp.R
import com.wantime.wbangapp.databinding.UiAdapterPernoalNullItemBinding
import com.wantime.wbangapp.databinding.UiAdapterRecordItemBinding
import com.wantime.wbangapp.databinding.UiAdapterSingleRecordHeadBinding
import com.wantime.wbangapp.inter.AdapterManager
import com.wantime.wbangapp.model.LoginAuthorityBean
import com.wantime.wbangapp.utils.ImageUtils

//单个授权记录的适配器
class SingleRecordAdapterRecycler(
    _context: Context,
    _mItemClick: RecyclerItemClick,
    _mHeadClick: RecyclerItemClick
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), AdapterManager {

    private val dataList = ArrayList<LoginAuthorityBean.ListBean.RecordsBean>()
    private val copyDataList = ArrayList<LoginAuthorityBean.ListBean.RecordsBean>()

    private var context: Context = _context
    private var mItemClick: RecyclerItemClick = _mItemClick
    private var mHeadClick: RecyclerItemClick = _mHeadClick

    private var headBean: LoginAuthorityBean.ListBean.RecordsBean? = null

    private val ITEM_TYPE_HEADER = 0
    private val ITEM_TYPE_CONTENT = 1
    private val ITEM_TYPE_BOTTOM = 2

    private var mHeaderCount = 1 //头部View个数
    private var mBottomCount = 1 //底部View个数

    fun pushData(_dataList: ArrayList<LoginAuthorityBean.ListBean.RecordsBean>) {
        this.dataList.addAll(_dataList)
        this.copyDataList.addAll(dataList)
    }

    fun pushHeadData(headBean: LoginAuthorityBean.ListBean.RecordsBean?) {
        this.headBean = headBean
    }

    fun onGetItemData(mPosition: Int): LoginAuthorityBean.ListBean.RecordsBean? {
        if (mPosition > -1 && mPosition < itemCount)
            return dataList[mPosition]
        return null
    }

    fun onFilter(text: String) {
        if (!TextUtils.isEmpty(text)) {
            dataList.clear()
            for (item in copyDataList) {
                if ((item.info != null && item.info.contains(text)))
                    dataList.add(item)
            }
            notifyDataSetChanged()
        } else {
            dataList.clear()
            dataList.addAll(copyDataList)
            notifyDataSetChanged()
        }
    }

    override fun onClear() {
        this.dataList.clear()
        this.copyDataList.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TYPE_HEADER -> {
                val binding = DataBindingUtil.inflate(
                    LayoutInflater.from(context),
                    R.layout.ui_adapter_single_record_head,
                    parent,
                    false
                ) as UiAdapterSingleRecordHeadBinding
                HeadRowViewHolder(binding)
            }
            else -> {
                val binding = DataBindingUtil.inflate(
                    LayoutInflater.from(context),
                    R.layout.ui_adapter_record_item,
                    parent,
                    false
                ) as UiAdapterRecordItemBinding
                RecordItemViewHolder(binding)

            }
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


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RecordItemViewHolder) {
            holder.getBinding().itemBean = dataList[position-mHeaderCount]
            holder.getBinding().itemClick = mItemClick
            holder.getBinding().executePendingBindings()
        } else if (holder is HeadRowViewHolder) {
            val tipFrontView =
                holder.getBinding().root.findViewById<TextView>(R.id.ui_single_tip_front)
            val singleHeadItem =
                holder.getBinding().root.findViewById<ConstraintLayout>(R.id.singleHeadItem)
            if (headBean != null) {
                tipFrontView.visibility = View.VISIBLE
                singleHeadItem.visibility = View.VISIBLE
                ImageUtils.loadImage(
                    context,
                    headBean?.imageUrl,
                    singleHeadItem.findViewById(R.id.recordItem)
                )
                singleHeadItem.findViewById<TextView>(R.id.recordName).text =
                    headBean?.platformName
                singleHeadItem.findViewById<TextView>(R.id.recordPrice).text =
                    context.getString(R.string.ui_kou_fei) + headBean?.price
                singleHeadItem.findViewById<TextView>(R.id.recordTime).text = headBean!!.createTime
                singleHeadItem.findViewById<TextView>(R.id.recordType).text = headBean!!.typeName
                singleHeadItem.findViewById<TextView>(R.id.IdText).text =
                    if (headBean!!.taskName.isNotEmpty()) (context.getString(R.string.ui_id) + headBean!!.taskId) else ""
                singleHeadItem.findViewById<TextView>(R.id.nickName).text =
                    if (headBean!!.nickName.isNotEmpty()) (context.getString(R.string.ui_nick) + headBean!!.wxUserNickname) else ""
                singleHeadItem.setOnClickListener {
                    mHeadClick.onItemClick(
                        singleHeadItem,
                        headBean!!
                    )
                }
            } else {
                tipFrontView.visibility = View.GONE
                singleHeadItem.visibility = View.GONE
            }

        }
    }

    //头部 ViewHolder
    internal class HeadRowViewHolder(private val binding: UiAdapterSingleRecordHeadBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun getBinding(): UiAdapterSingleRecordHeadBinding {
            return binding
        }
    }

    //数据行处理
    internal class RecordItemViewHolder(private val binding: UiAdapterRecordItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun getBinding(): UiAdapterRecordItemBinding {
            return binding
        }
    }

}