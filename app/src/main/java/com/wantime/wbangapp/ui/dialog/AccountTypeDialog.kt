package com.wantime.wbangapp.ui.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import com.block.dog.common.util.ToastUtil
import com.wantime.wbangapp.R
import com.wantime.wbangapp.model.AccountBean
import com.wantime.wbangapp.ui.adapter.AccountAdapterRecycler
import com.wantime.wbangapp.ui.adapter.RecyclerItemClick
import com.wantime.wbangapp.utils.Constants
import kotlinx.android.synthetic.main.ui_dialog_message_account.*
import kotlin.properties.Delegates

class AccountTypeDialog(context: Context) : Dialog(context, R.style.theme_dialog_alert) {
    private var mAdapter: AccountAdapterRecycler by Delegates.notNull()


    init {
        initView(context)
    }

    private fun initView(context: Context) {
        setContentView(R.layout.ui_dialog_message_account)
        val accountArray = context.resources.getStringArray(R.array.new_ui_account_menus)
        val tempList = ArrayList<AccountBean>()
        for ((index, accountItem) in accountArray.withIndex()) {
            val accountBean = AccountBean()
            accountBean.Title = accountItem
            accountBean.position = index + 1
            accountBean.isSelect = Constants.accountType== accountBean.position
            tempList.add(accountBean)
        }
        mAdapter = AccountAdapterRecycler(context, object : RecyclerItemClick {
            override fun onItemClick(itemView: View, iBean: Any) {
                ToastUtil.show(context, context.getString(R.string.new_ui_account_change_finish))
                iBean as AccountBean
                Constants.accountType = (iBean.position) % (accountArray.size+1)
                Constants.setSystemParam(context,"accountType",Constants.accountType)
                mAdapter.notifyDataSetChanged()
                dismiss()
            }
        })
        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(context, R.drawable.custom_divider)!!)
        mRecyclerView.addItemDecoration(divider)
        mRecyclerView.adapter = mAdapter
        mAdapter.pushDataList(tempList)
        mAdapter.notifyDataSetChanged()
    }


}