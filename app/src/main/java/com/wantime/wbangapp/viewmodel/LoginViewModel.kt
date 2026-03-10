package com.wantime.wbangapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSON
import com.wantime.wbangapp.common.BaseViewModel
import com.wantime.wbangapp.inter.IObserver
import com.wantime.wbangapp.model.*
import com.wantime.wbangapp.request.RetrofitManager
import com.wantime.wbangapp.utils.Constants
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody


class LoginViewModel(application: Application) : BaseViewModel(application) {


    fun onRegisterUser(
        username: String,
        password: String,
        inviteCode: String,
        verifyCode: String
    ): MutableLiveData<PostBackBean> {
        val liveData: MutableLiveData<PostBackBean> = MutableLiveData()
        val postBean = RegisterBean()
        postBean.phone = username
        postBean.password = password
        postBean.inviteCode = inviteCode
        postBean.verifyCode = verifyCode

        RetrofitManager.getInstance().apiRequest.onRegisterUser(postBean)
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


    fun onLogin(iUserBean: UserBean): MutableLiveData<String> {
        val liveData: MutableLiveData<String> = MutableLiveData<String>()
        RetrofitManager.getInstance().apiRequest.onLogin(iUserBean).subscribeOn(Schedulers.io())
            .subscribe(object : IObserver<String>() {
                override fun onNext(t: String) {
                    liveData.postValue(t)
                }

                override fun onError(e: Throwable) {
                    Log.e("faf", ">>>>>>>>>>>>>")
                }
            })
        return liveData
    }


    fun onAdLaunch(): MutableLiveData<AdItemBean> {
        val liveData: MutableLiveData<AdItemBean> = MutableLiveData<AdItemBean>()
        RetrofitManager.getInstance().apiRequest.onGetLaunchAD().subscribeOn(Schedulers.io())
            .flatMap {
                var itemBean = AdItemBean()
                Constants.onGetInfo(it).apply {
                    when (optInt("code")) {
                        Constants.NET_OK -> {
                            val dataJson = getJSONObject("data")
                            itemBean = JSON.parseObject(dataJson.toString(), AdItemBean::class.java)
                        }
                    }
                }
                Observable.fromArray(itemBean)
            }
            .subscribe(object : IObserver<AdItemBean>() {
                override fun onNext(t: AdItemBean) {
                    liveData.postValue(t)
                }
            })
        return liveData
    }


    fun checkUpdate(): MutableLiveData<String> {
        val liveData: MutableLiveData<String> = MutableLiveData<String>()
        RetrofitManager.getInstance().apiRequest.onGetLaunchAD().subscribeOn(Schedulers.io())
            .subscribe(object : IObserver<String>() {
                override fun onNext(t: String) {
                    liveData.postValue(t)
                }
            })
        return liveData
    }


    //免责申明
    fun onProtocol(): MutableLiveData<PostBackBean> {
        val liveData: MutableLiveData<PostBackBean> = MutableLiveData()
        RetrofitManager.getInstance().apiRequest.onProtocol()
            .subscribeOn(Schedulers.io()).flatMap {
                val iBean = PostBackBean()
                val mContent = it
                Constants.onGetInfo(mContent).apply {
                    iBean.code = optInt("code")
                    if(iBean.code==Constants.NET_OK){
                        val dataJson=optJSONObject("data")
                        iBean.message = dataJson.optString("userAgreement")
                    }

                }
                Observable.fromArray(iBean)
            }.subscribe(object : IObserver<PostBackBean>() {
                override fun onNext(t: PostBackBean) {
                    liveData.postValue(t)
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