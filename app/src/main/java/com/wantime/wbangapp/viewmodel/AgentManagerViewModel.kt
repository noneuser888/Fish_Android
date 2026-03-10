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

class AgentManagerViewModel(application: Application) : BaseViewModel(application) {
    private val pageSize: Int = 20 //每页主要20条
    private var mTotalPage = 1000000
    private var currentPage = 1 //当前分页

    fun onUserUpgrade(id: String): MutableLiveData<PostBackBean> {
        val liveData: MutableLiveData<PostBackBean> = MutableLiveData()
        RetrofitManager.getInstance().apiRequest.userUpgrade(id)
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


    fun onBannedUser(id: String): MutableLiveData<PostBackBean> {
        val liveData: MutableLiveData<PostBackBean> = MutableLiveData()
        RetrofitManager.getInstance().apiRequest.bannedUser(id)
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


    fun onAddRemark(id: String, remark: String): MutableLiveData<PostBackBean> {
        val liveData: MutableLiveData<PostBackBean> = MutableLiveData()
        RetrofitManager.getInstance().apiRequest.addRemark(id, remark)
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


    fun onRefreshAgentManager(searchKeys: String): MutableLiveData<AgentMangerBean> {
        currentPage = 1
        return onGetAgentManager(searchKeys, 1)
    }

    fun onLoadMoreAgentManager(searchKeys: String): MutableLiveData<AgentMangerBean> {
        return onGetAgentManager(searchKeys, ++currentPage)
    }


    fun onRefreshUserManager(searchKeys: String): MutableLiveData<AgentMangerBean> {
        currentPage = 1
        return onGetUserManager(searchKeys, 1)
    }

    fun onLoadMoreUserManager(searchKeys: String): MutableLiveData<AgentMangerBean> {
        return onGetUserManager(searchKeys, ++currentPage)
    }

    //代理列表
    private fun onGetAgentManager(searchKeys: String, page: Int): MutableLiveData<AgentMangerBean> {
        val liveData: MutableLiveData<AgentMangerBean> = MutableLiveData<AgentMangerBean>()
        RetrofitManager.getInstance().apiRequest.onMyAgencyList(searchKeys, page, pageSize)
            .subscribeOn(Schedulers.io()).flatMap {
                var iBean: AgentMangerBean? = null
                val mContent = it
                Constants.onGetInfo(mContent).apply {
                    when (optInt("code")) {
                        Constants.NET_OK -> {
                            iBean = JSON.parseObject(getString("data"), AgentMangerBean::class.java)
                            mTotalPage = iBean?.pages!!
                        }
                    }
                }
                if (iBean == null) iBean = AgentMangerBean()
                Observable.fromArray(iBean!!)
            }.subscribe(object : IObserver<AgentMangerBean>() {
                override fun onNext(t: AgentMangerBean) {
                    liveData.postValue(t)
                }
            })

        return liveData
    }

    //用户列表
    private fun onGetUserManager(searchKeys: String, page: Int): MutableLiveData<AgentMangerBean> {
        val liveData: MutableLiveData<AgentMangerBean> = MutableLiveData<AgentMangerBean>()
        RetrofitManager.getInstance().apiRequest.onMyUserList(searchKeys, page, pageSize)
            .subscribeOn(Schedulers.io()).flatMap {
                var iBean: AgentMangerBean? = null
                val mContent = it
                Constants.onGetInfo(mContent).apply {
                    when (optInt("code")) {
                        Constants.NET_OK -> {
                            iBean =
                                JSON.parseObject(getString("data"), AgentMangerBean::class.java)
                            mTotalPage = iBean?.pages!!
                        }
                    }
                }
                if (iBean == null) iBean = AgentMangerBean()
                Observable.fromArray(iBean!!)
            }.subscribe(object : IObserver<AgentMangerBean>() {
                override fun onNext(t: AgentMangerBean) {
                    liveData.postValue(t)
                }
            })

        return liveData
    }

}