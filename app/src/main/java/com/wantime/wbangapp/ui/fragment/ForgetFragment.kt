package com.wantime.wbangapp.ui.fragment

import android.graphics.Color
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.block.dog.common.util.ToastUtil
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseFragment
import com.wantime.wbangapp.databinding.ForgetFragmentBinding
import com.wantime.wbangapp.inter.IObserver
import com.wantime.wbangapp.utils.Constants
import com.wantime.wbangapp.viewmodel.ForgetViewModel
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.forget_fragment.*
import kotlinx.android.synthetic.main.ui_top_navigation.*
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

class ForgetFragment : BaseFragment<ForgetViewModel, ForgetFragmentBinding>() {


    override val layoutResource: Int
        get() = R.layout.forget_fragment


    override fun afterInitView() {
        initEventAndView()
    }

    private fun initEventAndView() {
        navBack.visibility = View.VISIBLE
        navTitle.text = getString(R.string.find_password)
        navBack.setOnClickListener { activity!!.finish() }
        forgetButton.setOnClickListener {
            if (TextUtils.isEmpty(userValue.text)) {
                ToastUtil.show(activity!!, getString(R.string.please_input_account))
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(passValue.text)) {
                ToastUtil.show(activity!!, getString(R.string.please_input_password))
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(vertifyValue.text)) {
                ToastUtil.show(activity!!, getString(R.string.please_input_vertify))
                return@setOnClickListener
            }
            onForgetSubmit(userValue.text.toString(),vertifyValue.text.toString(),passValue.text.toString())
        }

        vertifyTip.setOnClickListener {
            if (TextUtils.isEmpty(userValue.text)) {
                ToastUtil.show(activity!!, getString(R.string.please_input_account))
                return@setOnClickListener
            }
            onSendSms(userValue.text.toString())
            onStartTimer()
        }


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
                        Log.e("onComplete", ">>>>>>>>>>>>>>>>>>>$timeCounter")
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

    override fun onProcessLogic() {

    }


    //send email
    private fun onSendSms(userName: String) {
        showProgress()
        mViewModel?.onSendSms(userName)?.observe(this, Observer {
            hiddenProgress()
            ToastUtil.show(activity!!, it.message)
        })
    }

    //submit
    private fun onForgetSubmit(userName: String, code: String, passWord: String) {
        showProgress()
        mViewModel?.onResetPassword(userName, code, passWord)?.observe(this, Observer {
            hiddenProgress()
            ToastUtil.show(activity!!, it.message)
            if (it.ok == Constants.NET_OK) activity!!.finish()
        })
    }
}