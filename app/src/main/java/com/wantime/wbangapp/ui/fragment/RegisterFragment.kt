package com.wantime.wbangapp.ui.fragment

import android.text.Html
import android.text.TextUtils
import androidx.lifecycle.Observer
import com.block.dog.common.util.ToastUtil
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseFragment
import com.wantime.wbangapp.databinding.FragmentRegisterBinding
import com.wantime.wbangapp.model.bind.IRegisterBindBean
import com.wantime.wbangapp.ui.dialog.ProtolDialog
import com.wantime.wbangapp.utils.Constants
import com.wantime.wbangapp.viewmodel.RegisterViewModel
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_register.mCheckBox
import kotlinx.android.synthetic.main.fragment_register.mCheckBoxTip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RegisterFragment : BaseFragment<RegisterViewModel, FragmentRegisterBinding>() {

    private var mDialog: ProtolDialog? = null
    override val layoutResource: Int
        get() = R.layout.fragment_register

    override fun afterInitView() {
        mCheckBoxTip.text = Html.fromHtml("已阅读并同意<font color='#3268F0'>《橘子任务平台注册协议》</font>")
        mDialog = ProtolDialog(activity!!)
        mSubmitButton.setOnClickListener { onSubmitData() }
        mCheckBoxTip.setOnClickListener { mDialog?.show() }
        binding?.iItemBean = IRegisterBindBean()
    }

    override fun onProcessLogic() {
        mViewModel?.onUserAgreement()?.observe(this, Observer {
            if (it.code == Constants.NET_OK) {
                GlobalScope.launch(Dispatchers.Main) {
                    mDialog?.setContent(it.message)
                }
            }
        })
    }


    private fun onSubmitData() {
        if (!mCheckBox.isChecked) {
            ToastUtil.show(activity!!, "请先同意《橘子任务平台注册协议》！")
            return
        }
        if (TextUtils.isEmpty(binding?.iItemBean!!.username.get())) {
            ToastUtil.show(activity!!, "请填写姓名！")
            return
        }

        if (TextUtils.isEmpty(binding?.iItemBean!!.password.get())) {
            ToastUtil.show(activity!!, "密码不能为空！")
            return
        }
        if (TextUtils.isEmpty(binding?.iItemBean!!.inviteCode.get())) {
            ToastUtil.show(activity!!, "邀请码不能为空！")
            return
        }
        if (!binding?.iItemBean!!.password.get().equals(againPass.text.toString())) {
            ToastUtil.show(activity!!, "两次输入密码不一样！")
            return
        }

        mViewModel?.onRegisterUser(binding?.iItemBean!!.username.get().toString()
            ,binding?.iItemBean!!.password.get().toString()
            ,binding?.iItemBean!!.inviteCode.get().toString())
            ?.observe(this,
                Observer {
                    ToastUtil.show(activity!!, it.message)
                    if (it.code == Constants.NET_OK) {
                        activity!!.finish()
                    }
                })
    }
}