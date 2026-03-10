package com.wantime.wbangapp.ui.fragment

import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseFragment
import com.wantime.wbangapp.databinding.NewUiUpgradeToAgentBinding
import com.wantime.wbangapp.utils.Constants
import com.wantime.wbangapp.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.new_ui_upgrade_to_agent.*

class UserUpgradeFragment :BaseFragment<LoginViewModel,NewUiUpgradeToAgentBinding>(){

    override val layoutResource: Int
        get() = R.layout.new_ui_upgrade_to_agent

    override fun afterInitView() {
        menuBack.setOnClickListener { activity!!.finish() }
        initUserInfo()
    }

    override fun onProcessLogic() {

    }


    private fun initUserInfo(){
        val xUserBean = Constants.onGetUserBaseInfoWithNoToken()
        binding?.xUserBean = xUserBean
        agentTip.text=getString(R.string.new_ui_upgrade_tips)
        agentTip.append(getString(R.string.new_ui_connection_up_tips))
        agentTip.append("\n昵称："+xUserBean.myAgencyNickname)
        agentTip.append("\n电话："+xUserBean.myAgency)
    }
}