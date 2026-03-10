package com.wantime.wbangapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSON
import com.wantime.wbangapp.common.BaseViewModel
import com.wantime.wbangapp.inter.IObserver
import com.wantime.wbangapp.model.FormPostBean
import com.wantime.wbangapp.model.NewsBean
import com.wantime.wbangapp.model.NullBean
import com.wantime.wbangapp.model.PlatformBean
import com.wantime.wbangapp.request.RetrofitManager
import com.wantime.wbangapp.utils.Constants
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody

class PlatformViewModel(application: Application) : BaseViewModel(application) {

    fun onGetPlatformInfo(): MutableLiveData<PlatformBean> {
        val liveData: MutableLiveData<PlatformBean> = MutableLiveData<PlatformBean>()
        val postBean = FormPostBean()
        postBean.agencyId = Constants.onGetUserBaseInfoWithToken().agencyId
        RetrofitManager.getInstance().apiRequest.onPlatform(postBean).subscribeOn(Schedulers.io())
            .subscribe(object : IObserver<String>() {
                override fun onNext(t: String) {
                    Log.e("onNext",""+t)
                    liveData.postValue(covertToItemBean(t))
                }

                override fun onError(e: Throwable) {
                    Log.e("Throwable", ">>>>>>>>>>>>>" + e.message)
                }

                override fun onSubscribe(d: Disposable) {
                    Log.e("onSubscribe", ">>>>>>>>>>>>>" + d.toString())
                }

                override fun onComplete() {
                    Log.e("onComplete", ">>>>>>>>>>>>>onComplete" )
                }
            })

        return liveData
    }

    private fun covertToItemBean(content: String): PlatformBean {
        var iBean: PlatformBean? = null
        Constants.onGetInfo(content).apply {
            when (optInt("code")) {
                Constants.NET_OK -> {
                    iBean = JSON.parseObject(getString("data"), PlatformBean::class.java)
                }
            }
        }
        if (iBean == null) iBean = PlatformBean()
        return iBean!!
    }
}