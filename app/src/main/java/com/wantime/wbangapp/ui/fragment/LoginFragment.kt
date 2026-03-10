package com.wantime.wbangapp.ui.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Observer
import com.block.dog.common.util.ToastUtil
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseFragment
import com.wantime.wbangapp.databinding.FragmentLoginBinding
import com.wantime.wbangapp.inter.IObserver
import com.wantime.wbangapp.model.NullBean
import com.wantime.wbangapp.model.UserBean
import com.wantime.wbangapp.model.bind.IBindUserBean
import com.wantime.wbangapp.model.bind.IRegisterBindBean
import com.wantime.wbangapp.request.RetrofitManager
import com.wantime.wbangapp.ui.activity.ForgetActivity
import com.wantime.wbangapp.ui.activity.MainActivity
import com.wantime.wbangapp.ui.dialog.ProtolDialog
import com.wantime.wbangapp.ui.event.IFormListener
import com.wantime.wbangapp.ui.event.MessageEvent
import com.wantime.wbangapp.utils.Constants
import com.wantime.wbangapp.viewmodel.LoginViewModel
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit


class LoginFragment : BaseFragment<LoginViewModel, FragmentLoginBinding>() {

    //用户没有实名认证则提示
//    private var messageDialog: MessageDialog? = null
    private var mDialog: ProtolDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override val layoutResource: Int
        get() = R.layout.fragment_login

    override fun afterInitView() {

//        messageDialog = MessageDialog(activity!!)
//            .setTitle(getString(R.string.ui_system_title))
//            .setContent(getString(R.string.ui_not_real_name_tips))
//            .setConfirm("去认证")
//            .addConfirmListener(View.OnClickListener {
//                messageDialog?.dismiss()
//                Constants.onGotoIdCardVerif(activity!!)
//            })
//            .addCancelListener(View.OnClickListener {
//                onEnterMain()
//            })
//        messageDialog!!.setCancelable(false)

        vertifyTip.setOnClickListener {

            if (TextUtils.isEmpty(loginPhone.text)) {
                ToastUtil.show(activity!!, getString(R.string.new_ui_phone_not_null))
                return@setOnClickListener
            }
            showProgress()
            mViewModel?.onSendSms(loginPhone.text.toString())
                ?.observe(this,
                    Observer {
                        ToastUtil.show(activity!!, it.message)
                        hiddenProgress()
                    })
            onStartTimer()
        }

        mDialog = ProtolDialog(activity!!)
        uiSwitchButton.setOnCheckedChangeListener { _, b ->
            if (!b) {
                login_input_layout.visibility = View.VISIBLE
                register_input_layout.visibility = View.GONE
            } else {
                login_input_layout.visibility = View.GONE
                register_input_layout.visibility = View.VISIBLE
            }
        }
    }

    override fun onProcessLogic() {
        mViewModel?.onProtocol()?.observe(this, Observer {
            if (it.code == Constants.NET_OK) {
                GlobalScope.launch(Dispatchers.Main) {
                    mDialog?.setContent(it.message)
                }
            }
        })
        protolText1.setOnClickListener { mDialog?.show() }
        binding?.userBean = Constants.onGetUserInfo(activity!!)
        binding?.iItemBean = IRegisterBindBean()
        onBindEvent()
    }

    private fun onBindEvent() {
        binding?.iFormPost = object : IFormListener {
            override fun onFormPost(view: View, iBean: Any) {
                closeSoftKeybord(verificationCode, activity!!)
                if (iBean is IBindUserBean) {
                    onLogin()
                } else if (iBean is IRegisterBindBean) {
                    onRegister()
                }
            }
        }
        forgetView.setOnClickListener {
            startActivity(
                Intent(
                    activity,
                    ForgetActivity::class.java
                )
            )
        }
    }


    private fun onAutoPostForm() {
//        onEnterMain()
        if (binding?.userBean != null && !TextUtils.isEmpty(binding?.userBean!!.phone.get())) {
            val postBean = UserBean()
            postBean.phone = binding?.userBean!!.phone.get()!!
            postBean.password = binding?.userBean!!.password.get()!!
            showProgress()
            mViewModel?.onLogin(postBean)?.observe(this, androidx.lifecycle.Observer {
                Constants.onGetInfo(it).apply {
                    when (this.optInt("code")) {
                        Constants.NET_OK -> {
                            val baseInfo = getJSONObject("data")
                            if (baseInfo.optInt("status", 1) == 0) {
                                baseInfo.put("account", postBean.phone)
                                baseInfo.put("password", postBean.password)
                                Constants.onSaveUserBaseInfoWithToken(activity!!,baseInfo)
                                onEnterMain()
                            } else {
                                Constants.onSaveUserInfo(activity!!,UserBean())
                                ToastUtil.show(activity!!, optString("message"))
                            }
                        }
                        else -> {
                            Constants.onSaveUserInfo(activity!!,UserBean())
                            ToastUtil.show(activity!!, optString("message"))
                        }
                    }
                }
                hiddenProgress()
            })
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        when (event.mType) {
            MessageEvent.EventType.vertifyCheck.ordinal -> onEnterMain()
        }
    }

    private fun onEnterMain() {
        onRefreshUserInfo()
        startActivity(Intent(activity, MainActivity::class.java))
        Constants.sendRefreshCmd()
        activity!!.finish()
    }

    private fun onRefreshUserInfo() {
        RetrofitManager.getInstance().apiRequest.onUserInfo(NullBean()).subscribeOn(Schedulers.io())
            .subscribe(object : IObserver<String>() {
                override fun onNext(t: String) {
                    Constants.onGetInfo(t).apply {
                        when (optInt("code")) {
                            Constants.NET_OK -> {
                                Constants.sendRefreshCmd()
                                Constants.onSaveUserBaseInfoWithNoToken(getJSONObject("data"))
                            }
                        }
                    }
                }
            })
    }

    private fun onLogin() {
        if (!mCheckBox.isChecked) {
            ToastUtil.show(mActivity!!, getString(R.string.new_ui_agree_protol))
            return
        }
        if (TextUtils.isEmpty(binding?.userBean!!.phone.get())) {
            ToastUtil.show(mActivity!!, getString(R.string.new_ui_phone_not_null))
            return
        }
        if (TextUtils.isEmpty(binding?.userBean!!.password.get())) {
            ToastUtil.show(mActivity!!, getString(R.string.ui_tip_user_pass_not_null))
            return
        }
        onAutoPostForm()
    }

    private fun onRegister() {
        if (!mCheckBox.isChecked) {
            ToastUtil.show(activity!!, getString(R.string.new_ui_agree_protol))
            return
        }
        if (TextUtils.isEmpty(binding?.iItemBean!!.username.get())) {
            ToastUtil.show(activity!!, getString(R.string.new_ui_please_enter_name))
            return
        }

        if (TextUtils.isEmpty(binding?.iItemBean!!.password.get())) {
            ToastUtil.show(activity!!, getString(R.string.new_ui_pass_word_not_null))
            return
        }
        if (TextUtils.isEmpty(binding?.iItemBean!!.inviteCode.get())) {
            ToastUtil.show(activity!!, getString(R.string.new_ui_invite_code_not_null))
            return
        }

        /*if (TextUtils.isEmpty(binding?.iItemBean!!.verifyCode.get())) {
            ToastUtil.show(activity!!, getString(R.string.new_ui_vertify_code_not_null))
            return
        }*/

        if (!binding?.iItemBean!!.password.get().equals(registerPassAgain.text.toString())) {
            ToastUtil.show(activity!!, getString(R.string.new_ui_not_same_password))
            return
        }

        showProgress()
        mViewModel?.onRegisterUser(
            binding?.iItemBean!!.username.get().toString()
            , binding?.iItemBean!!.password.get().toString()
            , binding?.iItemBean!!.inviteCode.get().toString()
            , binding?.iItemBean!!.verifyCode.get().toString()
        )
            ?.observe(this, Observer {
                ToastUtil.show(activity!!, it.message)
                if (it.code == Constants.NET_OK)
                    GlobalScope.launch(Dispatchers.Main) {
                        uiSwitchButton.isChecked = false
                        ToastUtil.show(activity!!, getString(R.string.new_ui_please_login))
                    }
                hiddenProgress()
            })
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


    private fun onStartTimer() {
        if (vertifyTip.isEnabled) {
            vertifyTip.isEnabled = false
            var timeCounter = 60
            Observable.interval(0, 1, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
                .takeUntil<Long> {
                    timeCounter < 1
                }.subscribe(object : IObserver<Long>() {
                    override fun onNext(t: Long) {
                        if (activity != null && !activity!!.isFinishing) {
                            activity!!.runOnUiThread {
                                timeCounter--
                                if (timeCounter > 0) {
                                    vertifyTip.text = "($timeCounter)后重试"
                                    vertifyTip.setTextColor(Color.parseColor("#666666"))
                                }
                                if (timeCounter < 1) {
                                    onComplete()
                                    Schedulers.shutdown()
                                }
                            }
                        }
                    }

                    override fun onComplete() {
                        if (activity != null && !activity!!.isFinishing) {
                            activity!!.runOnUiThread {
                                if (timeCounter <= 0) {
                                    vertifyTip.text = getString(R.string.vertifyGet)
                                    vertifyTip.setTextColor(Color.parseColor("#16a3f9"))
                                    vertifyTip.isEnabled = true
                                }
                            }
                        }
                    }
                })
        }
    }
}