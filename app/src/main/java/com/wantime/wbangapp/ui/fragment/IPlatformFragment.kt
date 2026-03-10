package com.wantime.wbangapp.ui.fragment

import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import com.block.dog.common.util.ToastUtil
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseFragment
import com.wantime.wbangapp.databinding.FragmentIplatformBinding
import com.wantime.wbangapp.model.PlatformApplyBean
import com.wantime.wbangapp.model.bind.IPlatformBindBean
import com.wantime.wbangapp.ui.event.IFormListener
import com.wantime.wbangapp.utils.Constants
import com.wantime.wbangapp.viewmodel.IPlatformViewModel
import kotlinx.android.synthetic.main.fragment_iplatform.*
import kotlinx.android.synthetic.main.ui_top_navigation.*

class IPlatformFragment : BaseFragment<IPlatformViewModel, FragmentIplatformBinding>() {

    companion object {
        private var mIPlatformFragment: IPlatformFragment? = null
        fun newInstance(): IPlatformFragment {
            if (mIPlatformFragment == null) {
                mIPlatformFragment = IPlatformFragment()
            }

            return mIPlatformFragment!!
        }
    }

    override val layoutResource: Int
        get() = R.layout.fragment_iplatform

    override fun afterInitView() {
        navBack.visibility = View.VISIBLE
        navBack.setOnClickListener { activity!!.finish() }
        navTitle.text = "新平台申请"
    }

    override fun onProcessLogic() {
        if (mParamJson != null) {
            val iBean = IPlatformBindBean()
            iBean.appid.set(mParamJson!!.optString("appid"))
            iBean.bundleid.set(mParamJson!!.optString("_mmessage_appPackage"))
            iBean.scope.set(mParamJson!!.optString("_wxapi_sendauth_req_scope"))
            iBean.wxNickname.set(mParamJson!!.optString("_wxapi_app_nick_name"))
            iBean.imageUrl.set(mParamJson!!.optString("_wxapi_app_icon_url"))
            binding?.iDataBean = iBean
        }
        binding?.iFormPost = object : IFormListener {
            override fun onFormPost(view: View, iBean: Any) {
                this@IPlatformFragment.onFormPost(view, iBean)
            }
        }
    }

    private fun onFormPost(view: View, iBean: Any) {
        val iItemBean = iBean as IPlatformBindBean
        if (TextUtils.isEmpty(iItemBean.platformName.get())) {
            ToastUtil.show(activity!!, getString(R.string.ui_platform_name_tips))
            return
        }
        if (TextUtils.isEmpty(iItemBean.contact.get())) {
            ToastUtil.show(activity!!, getString(R.string.ui_platform_contract_tips))
            return
        }

        val postBean = PlatformApplyBean()
        postBean.appId = iItemBean.appid.get()!!
        postBean.platformName = if(TextUtils.isEmpty(iItemBean.platformName.get())) "" else iItemBean.platformName.get()!!
        postBean.bundleId = iItemBean.bundleid.get()!!
        postBean.scope = iItemBean.scope.get()!!
        postBean.type =
            if (loginType.isChecked) getString(R.string.ui_platform_login) else getString(R.string.ui_platform_proxy)
        postBean.nickname = iItemBean.wxNickname.get()!!
        postBean.imageUrl = iItemBean.imageUrl.get()!!
        postBean.description = iItemBean.description.get()!!
        postBean.contact = iItemBean.contact.get()!!
        postBean.device = iItemBean.device.get()!!

        mViewModel?.onIPlatformApply(postBean)!!.observe(this, Observer {
            ToastUtil.show(mActivity!!, it.message)
            when (it.ok) {
                Constants.NET_OK -> mActivity?.finish()
            }
        })
    }
}