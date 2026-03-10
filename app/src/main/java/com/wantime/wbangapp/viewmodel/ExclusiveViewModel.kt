package com.wantime.wbangapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSON
import com.wantime.wbangapp.common.BaseViewModel
import com.wantime.wbangapp.inter.IObserver
import com.wantime.wbangapp.model.AppealRecordBean
import com.wantime.wbangapp.model.ExclusiveBean
import com.wantime.wbangapp.model.FormPostBean
import com.wantime.wbangapp.model.NewsBean
import com.wantime.wbangapp.request.RetrofitManager
import com.wantime.wbangapp.utils.Constants
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody

class ExclusiveViewModel(application: Application) : BaseViewModel(application) {


    fun onRefreshNews(): MutableLiveData<ExclusiveBean> {
        return onGetExclusivePlatformList()
    }


    private fun onGetExclusivePlatformList(): MutableLiveData<ExclusiveBean> {
        val iFormBean = FormPostBean()
        iFormBean.agencyId = Constants.onGetUserBaseInfoWithToken().agencyId

        val liveData: MutableLiveData<ExclusiveBean> = MutableLiveData<ExclusiveBean>()
        RetrofitManager.getInstance().apiRequest.onExclusivePlatformList(iFormBean).subscribeOn(
            Schedulers.io()
        ).flatMap {
            var iBean: ExclusiveBean? = null
            val mContent = it
            Constants.onGetInfo(mContent).apply {
                when (optInt("code")) {
                    Constants.NET_OK -> {
                        iBean = JSON.parseObject(getString("data"), ExclusiveBean::class.java)
                    }
                }
            }
            if (iBean == null) iBean = ExclusiveBean()
            Observable.fromArray(iBean!!)
        }.subscribe(object : IObserver<ExclusiveBean>() {
            override fun onNext(t: ExclusiveBean) {
                liveData.postValue(t)
            }
        })

        return liveData
    }

}