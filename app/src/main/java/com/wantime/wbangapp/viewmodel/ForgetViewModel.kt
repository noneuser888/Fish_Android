package com.wantime.wbangapp.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.wantime.wbangapp.common.BaseViewModel
import com.wantime.wbangapp.inter.IObserver
import com.wantime.wbangapp.model.FindPostBean
import com.wantime.wbangapp.model.ForgetBean
import com.wantime.wbangapp.model.PostBackBean
import com.wantime.wbangapp.model.SendMsgBean
import com.wantime.wbangapp.request.RetrofitManager
import com.wantime.wbangapp.utils.Constants
import com.wantime.wbangapp.utils.NetCode
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class ForgetViewModel(application: Application) : BaseViewModel(application) {

    fun onSendEmail(userName: String): MutableLiveData<PostBackBean> {
        val liveData: MutableLiveData<PostBackBean> = MutableLiveData<PostBackBean>()
        val iBean = FindPostBean()
        iBean.username=userName
        RetrofitManager.getInstance().apiRequest.onSendEmail(iBean).subscribeOn(Schedulers.io())
            .subscribe(object : IObserver<String>() {
                override fun onNext(t: String) {
                    val postBack = PostBackBean()
                    Constants.onGetInfo(t).apply {
                        postBack.ok = optInt("code")
                        postBack.message = optString("message")
                    }
                    liveData.postValue(postBack)
                }
            })
        return liveData
    }


    fun onResetPassword(
        userName: String,
        code: String,
        passWord: String
    ): MutableLiveData<PostBackBean> {
        val liveData: MutableLiveData<PostBackBean> = MutableLiveData<PostBackBean>()
        val iBean = ForgetBean()
        iBean.phone = userName
        iBean.verifyCode = code
        iBean.password = passWord

        RetrofitManager.getInstance().apiRequest.onResetPassword(iBean).subscribeOn(Schedulers.io())
            .subscribe(object : IObserver<String>() {
                override fun onNext(t: String) {
                    val postBack = PostBackBean()
                    Constants.onGetInfo(t).apply {
                        postBack.ok = optInt("code",0)
                        postBack.message = optString("message","")
                    }
                    liveData.postValue(postBack)
                }
            })
        return liveData
    }


    //发送手机号码
    fun onSendSms(phone: String): MutableLiveData<PostBackBean> {
        val liveData: MutableLiveData<PostBackBean> = MutableLiveData()
        val postBean = SendMsgBean()
        postBean.phone = phone
        RetrofitManager.getInstance().apiRequest.sendSms(postBean)
            .subscribeOn(Schedulers.io()).flatMap {
                val iBean = PostBackBean()
                val mContent = it
                Constants.onGetInfo(mContent).apply {
                    iBean.code = optInt("code")
                    iBean.message = optString("message")
                }
                Observable.fromArray(iBean)
            }.subscribe(object : IObserver<PostBackBean>() {
                override fun onNext(t: PostBackBean) {
                    liveData.postValue(t)
                }
            })

        return liveData
    }

}