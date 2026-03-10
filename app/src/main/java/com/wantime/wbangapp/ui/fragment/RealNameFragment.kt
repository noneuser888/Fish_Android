package com.wantime.wbangapp.ui.fragment

import android.text.Html
import android.text.TextUtils
import androidx.lifecycle.Observer
import com.block.dog.common.util.ToastUtil
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseFragment
import com.wantime.wbangapp.databinding.FragmentRealnameBinding
import com.wantime.wbangapp.ui.dialog.ProtolDialog
import com.wantime.wbangapp.ui.event.MessageEvent
import com.wantime.wbangapp.utils.Constants
import com.wantime.wbangapp.viewmodel.RealNameViewModel
import kotlinx.android.synthetic.main.fragment_realname.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

class RealNameFragment : BaseFragment<RealNameViewModel, FragmentRealnameBinding>() {

    private var mDialog: ProtolDialog? = null
    override val layoutResource: Int
        get() = R.layout.fragment_realname

    override fun afterInitView() {
        mDialog = ProtolDialog(activity!!)
        mCheckBoxTip.text = Html.fromHtml("我已阅读实名认证<font color='#FF4D4D'>相关协议</font>")
        mCheckBoxTip.setOnClickListener { mDialog?.show() }
        mSubmitButton.setOnClickListener {
            onSubmitData()
        }
    }

    override fun onProcessLogic() {
        mViewModel?.onDisclaimer()?.observe(this, Observer {
            if (it.code == Constants.NET_OK) {
                GlobalScope.launch(Dispatchers.Main) {
                    mDialog?.setContent(it.message)
                }
            }
        })
    }


    private fun onSubmitData() {
        if (!mCheckBox.isChecked) {
            ToastUtil.show(activity!!, "请选择已阅读实名认证协议！")
            return
        }
        if (TextUtils.isEmpty(nameText.text)) {
            ToastUtil.show(activity!!, "请填写姓名！")
            return
        }
        if (TextUtils.isEmpty(codeText.text)) {
            ToastUtil.show(activity!!, "请填写身份证信息！")
            return
        }

        mViewModel?.onRealNameCheck(nameText.text.toString(), codeText.text.toString())
            ?.observe(this,
                Observer {
                    ToastUtil.show(activity!!, it.message)
                    if (it.code == Constants.NET_OK) {
                        val msg = MessageEvent()
                        msg.mType = MessageEvent.EventType.vertifyCheck.ordinal
                        EventBus.getDefault().post(msg)
                        activity!!.finish()
                    }
                })
    }
}