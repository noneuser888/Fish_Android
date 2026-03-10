package com.wantime.wbangapp.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSON
import com.wantime.wbangapp.common.BaseViewModel
import com.wantime.wbangapp.inter.IObserver
import com.wantime.wbangapp.model.AgentMangerBean
import com.wantime.wbangapp.model.PostBackBean
import com.wantime.wbangapp.model.RechargeRecordBean
import com.wantime.wbangapp.request.RetrofitManager
import com.wantime.wbangapp.utils.Constants
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlin.properties.Delegates

class RechargeRecordViewModel(application: Application) : BaseViewModel(application) {
    private val pageSize: Int = 20 //每页主要20条
    private var mTotalPage = 1000000
    private var currentPage = 1 //当前分页

    fun onRefreshRechargeRecord(): MutableLiveData<RechargeRecordBean> {
        currentPage = 1
        return onRechargeRecord(1)
    }

    fun onLoadMoreRechargeRecord(): MutableLiveData<RechargeRecordBean> {
        return onRechargeRecord(++currentPage)
    }


    fun onRecharge(userId: String, amount: String): MutableLiveData<PostBackBean> {
        val liveData: MutableLiveData<PostBackBean> = MutableLiveData()
        RetrofitManager.getInstance().apiRequest.rechargeUser(userId, amount)
            .subscribeOn(Schedulers.io()).flatMap {
                val iBean = PostBackBean()
                val mContent = it
                Constants.onGetInfo(mContent).apply {
                    iBean.ok = optInt("code")
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


    private fun onRechargeRecord(page: Int): MutableLiveData<RechargeRecordBean> {
        val liveData: MutableLiveData<RechargeRecordBean> = MutableLiveData()
        RetrofitManager.getInstance().apiRequest.onRechargeRecord(page, pageSize)
            .subscribeOn(Schedulers.io()).flatMap {
                var iBean: RechargeRecordBean? = null
                val mContent = it
                Constants.onGetInfo(mContent).apply {
                    when (optInt("code")) {
                        Constants.NET_OK -> {
                            iBean =
                                JSON.parseObject(getString("data"), RechargeRecordBean::class.java)
                            mTotalPage = iBean?.pages!!
                        }
                    }
                }
                if (iBean == null) iBean = RechargeRecordBean()
                Observable.fromArray(iBean!!)
            }.subscribe(object : IObserver<RechargeRecordBean>() {
                override fun onNext(t: RechargeRecordBean) {
                    liveData.postValue(t)
                }
            })

        return liveData
    }

}