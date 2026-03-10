package com.wantime.wbangapp.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSON
import com.wantime.wbangapp.common.BaseViewModel
import com.wantime.wbangapp.inter.IObserver
import com.wantime.wbangapp.model.FormPostBean
import com.wantime.wbangapp.model.NewsBean
import com.wantime.wbangapp.model.PostBackBean
import com.wantime.wbangapp.request.RetrofitManager
import com.wantime.wbangapp.utils.Constants
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class AuthorityViewModel(application: Application) : BaseViewModel(application) {
    private val pageSize: Int = 20 //每页主要20条
    private var mTotalPage = 1000000
    private var currentPage = 1 //当前分页

    fun onRefreshNews(): MutableLiveData<NewsBean> {
        currentPage = 1
        return onGetTodayNews(1)
    }

    fun onLoadMore(): MutableLiveData<NewsBean> {
        return onGetTodayNews(++currentPage)
    }

    fun onUpdateNickname(nickname: String): MutableLiveData<PostBackBean> {
        val liveData: MutableLiveData<PostBackBean> = MutableLiveData()
        RetrofitManager.getInstance().apiRequest.updateNicknameUrl(nickname)
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

    fun onKeyCharge(key: String): MutableLiveData<PostBackBean> {
        val liveData: MutableLiveData<PostBackBean> = MutableLiveData()
        RetrofitManager.getInstance().apiRequest.keyRechargeUser(key)
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

    //批量注册子账户
    fun onRegisterBatchUser(number:Int): MutableLiveData<PostBackBean> {
        val liveData: MutableLiveData<PostBackBean> = MutableLiveData()
        RetrofitManager.getInstance().apiRequest.bathchRegister(number)
            .subscribeOn(Schedulers.io()).flatMap {
                val iBean = PostBackBean()
                val mContent = it
                Constants.onGetInfo(mContent).apply {
                    iBean.code = optInt("code")
                    iBean.message = optString("message")
                    if(has("data")){
                        val dataJson=getJSONObject("data")
                        if(dataJson.has("list"))
                            iBean.message=dataJson.optString("list")
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

    fun onGetAuthHomeList(): MutableLiveData<NewsBean> {
        val liveData: MutableLiveData<NewsBean> = MutableLiveData<NewsBean>()
        if (currentPage < mTotalPage)
            RetrofitManager.getInstance().apiRequest.onAuthHome(Constants.onGetUserBaseInfoWithToken().agencyId)
                .subscribeOn(Schedulers.io()).flatMap {
                    var iItemBean: NewsBean? = null
                    Constants.onGetInfo(it).apply {
                        when (optInt("code")) {
                            Constants.NET_OK -> {
                                val dataJson = getJSONObject("data")
                                iItemBean = JSON.parseObject(dataJson.toString(), NewsBean::class.java)
                            }
                        }
                    }
                    if (iItemBean == null) iItemBean = NewsBean()
                    Observable.fromArray(iItemBean!!)
                }.subscribe(object : IObserver<NewsBean>() {
                    override fun onNext(t: NewsBean) {
                        liveData.postValue(t)
                    }
                })
        else liveData.postValue(NewsBean())
        return liveData
    }

    private fun onGetTodayNews(page: Int): MutableLiveData<NewsBean> {
        val liveData: MutableLiveData<NewsBean> = MutableLiveData<NewsBean>()
        val postBean = FormPostBean()
        if (currentPage < mTotalPage)
            RetrofitManager.getInstance().apiRequest.onGetArticleList(page, pageSize, postBean)
                .subscribeOn(Schedulers.io()).flatMap {
                    var iItemBean: NewsBean? = null
                    Constants.onGetInfo(it).apply {
                        when (optInt("code")) {
                            Constants.NET_OK -> {
                                val dataJson = getJSONObject("data")
                                iItemBean =
                                    JSON.parseObject(dataJson.toString(), NewsBean::class.java)
                            }
                        }
                    }
                    if (iItemBean == null) iItemBean = NewsBean()
                    Observable.fromArray(iItemBean!!)
                }.subscribe(object : IObserver<NewsBean>() {
                    override fun onNext(t: NewsBean) {
                        liveData.postValue(t)
                    }
                })
        else liveData.postValue(NewsBean())
        return liveData
    }

    //退出登录
    fun onLoginOut(): MutableLiveData<PostBackBean> {
        val liveData: MutableLiveData<PostBackBean> = MutableLiveData()
        RetrofitManager.getInstance().apiRequest.userLogout()
            .subscribeOn(Schedulers.io()).flatMap {
                val iBean = PostBackBean()
                val mContent = it
                Constants.onGetInfo(mContent).apply {
                    iBean.code = optInt("code")
                    if(has("message"))
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