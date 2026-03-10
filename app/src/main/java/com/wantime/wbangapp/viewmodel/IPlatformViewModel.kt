package com.wantime.wbangapp.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.wantime.wbangapp.common.BaseViewModel
import com.wantime.wbangapp.inter.IObserver
import com.wantime.wbangapp.model.PlatformApplyBean
import com.wantime.wbangapp.model.PostBackBean
import com.wantime.wbangapp.request.RetrofitManager
import com.wantime.wbangapp.utils.Constants
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class IPlatformViewModel(application: Application) : BaseViewModel(application) {

    fun onIPlatformApply(iBean: PlatformApplyBean): MutableLiveData<PostBackBean> {
        val liveData = MutableLiveData<PostBackBean>()

        RetrofitManager.getInstance().apiRequest.onPlatformApply(iBean).flatMap {
            val postBean = PostBackBean()
            Constants.onGetInfo(it).apply {
                postBean.ok = optInt("code")
                postBean.message = optString("message")
            }
            Observable.fromArray(postBean)
        }.subscribeOn(Schedulers.io()).subscribe(object : IObserver<PostBackBean>() {
            override fun onNext(t: PostBackBean) {
                liveData.postValue(t)
            }
        })

        return liveData
    }
}