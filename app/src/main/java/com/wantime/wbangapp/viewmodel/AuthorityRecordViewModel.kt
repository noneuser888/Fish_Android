package com.wantime.wbangapp.viewmodel

import android.app.Application
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSON
import com.wantime.wbangapp.common.BaseViewModel
import com.wantime.wbangapp.inter.IObserver
import com.wantime.wbangapp.model.*
import com.wantime.wbangapp.request.RetrofitManager
import com.wantime.wbangapp.utils.Constants
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody

class AuthorityRecordViewModel(application: Application) : BaseViewModel(application) {
    private val pageSize: Int = 10000 //每页主要20条
    private var mTotalPage = 1000000
    private var mTotalPage1 = 1000000
    private var mTotalPage2 = 1000000


    private var currentPage = 1 //当前登录授权记录分页
    private var currentPage1 = 1 //当前专属授权记录分页
    private var currentPage2 = 1 //当前绑定授权记录分页

    fun onLoginAuthorityRefreshNews(appId: String,startTime:String,endTime:String): MutableLiveData<LoginAuthorityBean> {
        currentPage = 1
        return onGetLoginAuthorityRecord(appId, currentPage,startTime,endTime)
    }

    fun onLoginAuthorityLoadMore(appId: String,startTime:String,endTime:String): MutableLiveData<LoginAuthorityBean> {
        return onGetLoginAuthorityRecord(appId, ++currentPage,startTime,endTime)
    }


    fun onZSAuthorityRefreshNews(): MutableLiveData<LoginAuthorityBean> {
        currentPage1 = 1
        return onGetZSAuthorityRecord(currentPage1)
    }

    fun onZSAuthorityLoadMore(): MutableLiveData<LoginAuthorityBean> {
        return onGetZSAuthorityRecord(++currentPage1)
    }

    fun onBindAuthorityRefreshNews(): MutableLiveData<LoginAuthorityBean> {
        currentPage2 = 1
        return onGetBindAuthorityRecord(currentPage2)
    }

    fun onBindAuthorityLoadMore(): MutableLiveData<LoginAuthorityBean> {
        return onGetBindAuthorityRecord(++currentPage2)
    }

    //登录授权记录
    private fun onGetLoginAuthorityRecord(
        appId: String,
        page: Int,startTime:String,endTime:String
    ): MutableLiveData<LoginAuthorityBean> {
        val liveData: MutableLiveData<LoginAuthorityBean> = MutableLiveData<LoginAuthorityBean>()
        val postBean = FormPostBean()
        if (!TextUtils.isEmpty(appId))
            postBean.appId = appId
        if(!TextUtils.isEmpty(startTime))
            postBean.startTime=startTime
        if(!TextUtils.isEmpty(endTime))
            postBean.endTime=endTime

        RetrofitManager.getInstance().apiRequest.onLoginauthRecord(page, pageSize, postBean)
            .subscribeOn(Schedulers.io()).flatMap {
                var iBean: LoginAuthorityBean? = null
                val mContent = it
                Constants.onGetInfo(mContent).apply {
                    when (optInt("code")) {
                        Constants.NET_OK -> {
                            iBean =
                                JSON.parseObject(getString("data"), LoginAuthorityBean::class.java)
                            mTotalPage = iBean?.list!!.total
                        }
                    }
                }
                if (iBean == null) iBean = LoginAuthorityBean()
                Observable.fromArray(iBean!!)
            }.subscribe(object : IObserver<LoginAuthorityBean>() {
                override fun onNext(t: LoginAuthorityBean) {
                    liveData.postValue(t)
                }
            })

        return liveData
    }

    //专属授权记录
    private fun onGetZSAuthorityRecord(page: Int): MutableLiveData<LoginAuthorityBean> {
        val liveData: MutableLiveData<LoginAuthorityBean> = MutableLiveData<LoginAuthorityBean>()
        val iFormBean = FormPostBean()
        iFormBean.type = 5
        RetrofitManager.getInstance().apiRequest.onZSauthRecord(page, pageSize, iFormBean)
            .subscribeOn(Schedulers.io()).flatMap {
                var iBean: LoginAuthorityBean? = null
                val mContent = it
                Constants.onGetInfo(mContent).apply {
                    when (optInt("code")) {
                        Constants.NET_OK -> {
                            iBean =
                                JSON.parseObject(getString("data"), LoginAuthorityBean::class.java)
                            mTotalPage1 = iBean?.list!!.total
                        }
                    }
                }
                if (iBean == null) iBean = LoginAuthorityBean()
                Observable.fromArray(iBean!!)
            }.subscribe(object : IObserver<LoginAuthorityBean>() {
                override fun onNext(t: LoginAuthorityBean) {
                    liveData.postValue(t)
                }

                override fun onError(e: Throwable) {
                    Log.e("onError", e.message + "")
                }
            })

        return liveData
    }

    //绑定授权记录
    private fun onGetBindAuthorityRecord(page: Int): MutableLiveData<LoginAuthorityBean> {
        val liveData: MutableLiveData<LoginAuthorityBean> = MutableLiveData<LoginAuthorityBean>()
        RetrofitManager.getInstance().apiRequest.onPAQTaskList(page, pageSize, NullBean())
            .subscribeOn(Schedulers.io()).flatMap {
                var iBean: LoginAuthorityBean? = null
                val mContent = it
                Constants.onGetInfo(mContent).apply {
                    when (optInt("code")) {
                        Constants.NET_OK -> {
                            iBean =
                                JSON.parseObject(getString("data"), LoginAuthorityBean::class.java)
                            mTotalPage1 = iBean?.list!!.total
                        }
                    }
                }
                if (iBean == null) iBean = LoginAuthorityBean()
                Observable.fromArray(iBean!!)
            }.subscribe(object : IObserver<LoginAuthorityBean>() {
                override fun onNext(t: LoginAuthorityBean) {
                    liveData.postValue(t)
                }
            })

        return liveData
    }


    //订单添加备注
      fun onAddOrderRecordMark(
        authOrderId: String,
        remark: String
    ): MutableLiveData<PostBackBean> {
        val liveData: MutableLiveData<PostBackBean> = MutableLiveData()
        val postBean = OrderMarkInfo()
        postBean.authOrderId = authOrderId
        postBean.remark = remark
        RetrofitManager.getInstance().apiRequest.onAddMarkForOrder(postBean)
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

      //为订单申请售后
      fun onAfterSale(authOrderId: String): MutableLiveData<PostBackBean> {
        val liveData: MutableLiveData<PostBackBean> = MutableLiveData()
        RetrofitManager.getInstance().apiRequest.afterSale(authOrderId)
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

    //续费功能
    fun onRenewalAuth(
        appId: String,
        orderId: String,
        state: String,
        onkeyType: Int,
        modelType: Int,
        grade: Int
    ): MutableLiveData<PostBackBean> {
        val liveData: MutableLiveData<PostBackBean> = MutableLiveData<PostBackBean>()
        val formBean = AutoAuthBean()
        formBean.appId = appId
        formBean.qrCode = "7" // 续费标识
        formBean.id = orderId
        formBean.state = state
        formBean.type = onkeyType
        formBean.model = modelType
        formBean.grade = grade

        RetrofitManager.getInstance().apiRequest.onAutoAuth(formBean)
            .subscribeOn(Schedulers.io()).flatMap {
                val itemBean = PostBackBean()
                Constants.onGetInfo(it).apply {
                    itemBean.ok = optInt("code")
                    itemBean.message = optString("message")
                    when (itemBean.ok) {
                        Constants.NET_OK -> {
                            val dataJson = getJSONObject("data")
                            if (dataJson.has("authOrderId")) {
                                itemBean.authOrderId = dataJson.getString("authOrderId")
                                if (dataJson.has("code"))
                                    itemBean.redirect_url = dataJson.optString("code")
                            } else if (dataJson.has("orderId") || dataJson.has("code")) {
                                itemBean.authOrderId = dataJson.getString("orderId")
                                itemBean.redirect_url = dataJson.optString("code")
                            }
                        }
                    }
                }
                Observable.fromArray(itemBean)
            }.subscribe(object : IObserver<PostBackBean>() {
                override fun onNext(t: PostBackBean) {
                    liveData.postValue(t)
                }

                override fun onError(e: Throwable) {
                    Log.e("RenewalAuth onError", e.message + "")
                    val errorBean = PostBackBean()
                    errorBean.ok = Constants.NET_FAILED
                    errorBean.message = "网络错误: " + e.message
                    liveData.postValue(errorBean)
                }
            })

        return liveData
    }
}