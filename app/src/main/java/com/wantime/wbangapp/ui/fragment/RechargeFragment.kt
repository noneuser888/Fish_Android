package com.wantime.wbangapp.ui.fragment

import android.annotation.SuppressLint
import android.text.TextUtils
import android.text.method.DigitsKeyListener
import android.widget.TextView
import androidx.lifecycle.Observer
import com.alibaba.fastjson.JSON
import com.block.dog.common.util.ToastUtil
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseFragment
import com.wantime.wbangapp.databinding.NewUiRechargeForSomeoneBinding
import com.wantime.wbangapp.model.AgentMangerBean
import com.wantime.wbangapp.ui.dialog.MessageDialog
import com.wantime.wbangapp.utils.Constants
import com.wantime.wbangapp.viewmodel.RechargeRecordViewModel
import kotlinx.android.synthetic.main.new_ui_recharge_for_someone.*
import kotlinx.android.synthetic.main.ui_top_navigation_white.*
import kotlin.properties.Delegates

class RechargeFragment : BaseFragment<RechargeRecordViewModel, NewUiRechargeForSomeoneBinding>() {

    private var mMessageDialog: MessageDialog by Delegates.notNull()

    override val layoutResource: Int
        get() = R.layout.new_ui_recharge_for_someone

    override fun afterInitView() {
        mMessageDialog = MessageDialog(activity!!).setTitle(getString(R.string.ui_system_title))
        initUserInfo()
    }

    override fun onProcessLogic() {

    }

    @SuppressLint("SetTextI18n")
    private fun initUserInfo() {
        navTitle.text = getString(R.string.new_ui_recharge)
        navBack.setOnClickListener { activity!!.finish() }
        rechargeValue.keyListener=DigitsKeyListener.getInstance("0123456789")
        binding?.xUserBean = Constants.onGetUserBaseInfoWithNoToken()
        if (mParamJson != null) {
            val iBean =
                JSON.parseObject(mParamJson.toString(), AgentMangerBean.RecordsBean::class.java)
            userAccount.text = "账号：${iBean.phone} ${iBean.nickname}"

            rechargeGroup.setOnCheckedChangeListener { group, checkedId ->
                val checkText = rechargeGroup.findViewById<TextView>(checkedId)
                rechargeValue.setText(checkText.text.toString().replace("元", "").trim())
            }
            rechargeButton.setOnClickListener {
                if (TextUtils.isEmpty(rechargeValue.text)) {
                    ToastUtil.show(activity!!, getString(R.string.new_ui_please_set_money))
                    return@setOnClickListener
                }
                showProgress()
                mViewModel?.onRecharge(iBean.id, rechargeValue.text.toString())!!
                    .observe(activity!!,
                        Observer {
                            hiddenProgress()
                            ToastUtil.show(activity!!, it.message)
                            if (it.ok == Constants.NET_OK) {
                                Constants.sendRefreshUserBaseInfo()
                                activity!!.finish()
                            }
                        })
            }
        }


    }

}