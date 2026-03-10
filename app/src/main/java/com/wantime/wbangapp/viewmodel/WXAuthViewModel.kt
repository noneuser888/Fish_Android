package com.wantime.wbangapp.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.wantime.wbangapp.common.BaseViewModel
import com.wantime.wbangapp.inter.IObserver
import com.wantime.wbangapp.model.*
import com.wantime.wbangapp.request.APIRequestServiceImpl
import com.wantime.wbangapp.request.RetrofitManager
import com.wantime.wbangapp.request.ValueCallBack
import com.wantime.wbangapp.utils.Constants
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

//授权相关
class WXAuthViewModel(application: Application) : BaseViewModel(application) {

    //获取在线接单的数量
    fun onGetOnlineNumber(appId: String): MutableLiveData<PostBackBean> {
        val liveData: MutableLiveData<PostBackBean> = MutableLiveData<PostBackBean>()
        val formBean = FormPostBean()
        formBean.appId = appId
        RetrofitManager.getInstance().apiRequest.onGetOnlineNumber(formBean)
            .subscribeOn(Schedulers.io()).flatMap {
                val itemBean = PostBackBean()
                Constants.onGetInfo(it).apply {
                    when (optInt("code")) {
                        Constants.NET_OK -> {
                            val dataJson = getJSONObject("data")
                            itemBean.code = dataJson.optInt("totalCount", 0)
                        }
                    }
                    itemBean.message = optString("message")
                }
                Observable.fromArray(itemBean)
            }.subscribe(object : IObserver<PostBackBean>() {
                override fun onNext(t: PostBackBean) {
                    liveData.postValue(t)
                }
            })

        return liveData
    }

    //显示二维码扣费接口
    fun onQRCodeAuth(): MutableLiveData<PostBackBean> {
        val liveData: MutableLiveData<PostBackBean> = MutableLiveData<PostBackBean>()
        val formBean = FormPostBean()
        RetrofitManager.getInstance().apiRequest.onQRCodeAuth(formBean)
            .subscribeOn(Schedulers.io()).flatMap {
                val itemBean = PostBackBean()
                Constants.onGetInfo(it).apply {
                    when (optInt("code")) {
                        Constants.NET_OK -> {
                            val authOrderId = getString("data")
                            itemBean.authOrderId = authOrderId
                        }
                    }
                    itemBean.message = optString("message")
                    itemBean.ok = optInt("code")
                }

                Observable.fromArray(itemBean)
            }.subscribe(object : IObserver<PostBackBean>() {
                override fun onNext(t: PostBackBean) {
                    liveData.postValue(t)
                }
            })

        return liveData
    }

    //成功之后要更新记录
    fun updateRecord(iposeBean: UpdateRecordBean): MutableLiveData<PostBackBean> {
        val liveData: MutableLiveData<PostBackBean> = MutableLiveData<PostBackBean>()
        RetrofitManager.getInstance().apiRequest.updateRecord(iposeBean)
            .subscribeOn(Schedulers.io()).flatMap {
                val itemBean = PostBackBean()
                Constants.onGetInfo(it).apply {
                    itemBean.ok = optInt("code")
                    itemBean.message = optString("message")
                }
                Observable.fromArray(itemBean)
            }.subscribe(object : IObserver<PostBackBean>() {
                override fun onNext(t: PostBackBean) {
                    liveData.postValue(t)
                }
            })

        return liveData
    }

    //QR没有授权Life Owner
    fun onQRCodeAuthNoOwner(valueCallBack: ValueCallBack) {
        val formBean = FormPostBean()
        RetrofitManager.getInstance().apiRequest.onQRCodeAuth(formBean)
            .subscribeOn(Schedulers.io()).flatMap {
                val itemBean = PostBackBean()
                Constants.onGetInfo(it).apply {
                    when (optInt("code")) {
                        Constants.NET_OK -> {
                            val authOrderId = getString("data")
                            itemBean.authOrderId = authOrderId
                        }
                    }
                    itemBean.message = optString("message")
                }

                Observable.fromArray(itemBean)
            }.subscribe(object : IObserver<PostBackBean>() {
                override fun onNext(t: PostBackBean) {
                    valueCallBack.onValueBack(t)
                }
            })

    }

    //授权成功回调地址
    fun onQrCodeSuccess(
        authOrderId: String,
        redirect: String,
        wxUserNickname: String
    ): MutableLiveData<PostBackBean> {
        val liveData: MutableLiveData<PostBackBean> = MutableLiveData<PostBackBean>()
        val formBean = ConfirmOrderBean()
        formBean.authOrderId = authOrderId
        formBean.redirectUrl = redirect
        formBean.wxUserNickname = wxUserNickname

        RetrofitManager.getInstance().apiRequest.onConfirmOrder(formBean)
            .subscribeOn(Schedulers.io()).flatMap {
                val itemBean = PostBackBean()
                Constants.onGetInfo(it).apply {
                    itemBean.ok = optInt("code")
                    itemBean.message = optString("message")
                }
                Observable.fromArray(itemBean)
            }.subscribe(object : IObserver<PostBackBean>() {
                override fun onNext(t: PostBackBean) {
                    liveData.postValue(t)
                }
            })

        return liveData
    }


    fun onQrCodeSuccessNoOwner(
        authOrderId: String,
        redirect: String,
        wxUserNickname: String,
        valueCallBack: ValueCallBack
    ) {
        val formBean = ConfirmOrderBean()
        formBean.authOrderId = authOrderId
        formBean.redirectUrl = redirect
        formBean.wxUserNickname = wxUserNickname

        RetrofitManager.getInstance().apiRequest.onConfirmOrder(formBean)
            .subscribeOn(Schedulers.io()).flatMap {
                val itemBean = PostBackBean()
                Constants.onGetInfo(it).apply {
                    itemBean.ok = optInt("code")
                    itemBean.message = optString("message")
                }
                Observable.fromArray(itemBean)
            }.subscribe(object : IObserver<PostBackBean>() {
                override fun onNext(t: PostBackBean) {
                    valueCallBack.onValueBack(t)
                }
            })

    }

    //一键授权
    fun onKeyAuthorityRequest(
        qrCode: String,
        appId: String,
        orderId: String,
        state: String,
        onkeyType:Int,
        modelType:Int,
        grade:Int
    ): MutableLiveData<PostBackBean> {
        val liveData: MutableLiveData<PostBackBean> = MutableLiveData<PostBackBean>()
        val formBean = AutoAuthBean()
        formBean.appId = appId
        formBean.qrCode = qrCode
        formBean.id = orderId
        formBean.state = state
        formBean.type=onkeyType
        formBean.model=modelType
        formBean.grade=grade //Constants.accountType

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
                                if(dataJson.has("code"))
                                    itemBean.redirect_url = dataJson.optString("code")
                            } else if(dataJson.has("orderId")||dataJson.has("code")){
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
            })
        return liveData
    }

    //微信授权重新回调
    fun OnRequestWXAuth(wxUrl:String): MutableLiveData<PostBackBean>{
        val liveData: MutableLiveData<PostBackBean> = MutableLiveData()
        GlobalScope.launch(Dispatchers.IO){
            val mResultJson= APIRequestServiceImpl.getInstance().apiRequestNetGetSyncForCommon(wxUrl)
            val itemBean = PostBackBean()
            itemBean.ok=Constants.NET_FAILED
            if(!TextUtils.isEmpty(mResultJson)){
                val mArray= mResultJson.split(";")
                if (mArray.size>=3){
                    val firstItem =mArray[0]
                    if(firstItem.contains("405")){
                        for ( itemAuth in mArray){
                            //获取验证字段
                            if(itemAuth.contains("window.wx_redirecturl")) {
                                val authMessage=itemAuth.replace("window.wx_redirecturl=","")
                                itemBean.ok=Constants.NET_OK
                                itemBean.redirect_url=authMessage.replace("'","").replace(";","").trim()
                            }
                            //获取名称
                            if(itemAuth.contains("window.wx_nickname")){
                                val authMessage=itemAuth.replace("window.wx_nickname=","")
                                itemBean.ok=Constants.NET_OK
                                itemBean.message=authMessage.replace("'","").replace(";","").trim()
                            }
                        }
                    }
                }
            }
            liveData.postValue(itemBean)
        }
        return liveData
    }

    //网页授权登录
    fun onPCAuthorityRequest(
        qrCode: String,
        appId: String,
        orderId: String,
        state: String
    ): MutableLiveData<PostBackBean> {
        val liveData: MutableLiveData<PostBackBean> = MutableLiveData<PostBackBean>()
        val formBean = AutoAuthBean()
        formBean.appId = appId
        formBean.qrCode = qrCode
        formBean.id = orderId
        formBean.state = state

        RetrofitManager.getInstance().apiRequest.onScanCode(formBean)
            .subscribeOn(Schedulers.io()).flatMap {
                val itemBean = PostBackBean()
                Constants.onGetInfo(it).apply {
                    //                    when (optInt("code")) {
//                        Constants.NET_OK -> {
//                            itemBean.ok = Constants.NET_OK
//                            val dataJson = getJSONObject("data")
//                            itemBean.authOrderId = dataJson.getString("authOrderId")
//                        }
//                    }
                    itemBean.ok = optInt("code")
                    itemBean.message = optString("message")
                    when (itemBean.ok) {
                        Constants.NET_OK -> {
                            val dataJson = getJSONObject("data")
                            if (dataJson.has("authOrderId")) {
                                itemBean.authOrderId = dataJson.getString("authOrderId")
                            } else if (dataJson.has("code")) {
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
            })
        return liveData
    }

    //专属授权接口
    fun onExclusiveAuthRequest(
        qrCode: String,
        appId: String,
        uuid: String
    ): MutableLiveData<PostBackBean> {
        val liveData: MutableLiveData<PostBackBean> = MutableLiveData<PostBackBean>()
        val formBean = ExclusivePostBean()
        formBean.appId = appId
        formBean.qrCode = qrCode
        formBean.uuid = uuid
        RetrofitManager.getInstance().apiRequest.onExclusiveAuth(formBean)
            .subscribeOn(Schedulers.io()).flatMap {
                val itemBean = PostBackBean()
                Constants.onGetInfo(it).apply {
                    itemBean.ok = optInt("code")
                    itemBean.message = optString("message")
                    when (itemBean.ok) {
                        Constants.NET_OK -> {
                            val dataJson = getJSONObject("data")
                            itemBean.authOrderId = dataJson.getString("authOrderId")
                        }
                    }
                }
                Observable.fromArray(itemBean)
            }.subscribe(object : IObserver<PostBackBean>() {
                override fun onNext(t: PostBackBean) {
                    liveData.postValue(t)
                }
            })
        return liveData
    }


    //取消专属任务
    fun onCancelAuth(orderId: String): MutableLiveData<PostBackBean> {
        val liveData: MutableLiveData<PostBackBean> = MutableLiveData<PostBackBean>()

        RetrofitManager.getInstance().apiRequest.onCancelAuth(orderId)
            .subscribeOn(Schedulers.io()).flatMap {
                val itemBean = PostBackBean()
                Constants.onGetInfo(it).apply {
                    itemBean.ok = optInt("code")
                    itemBean.message = optString("message")
                }

                Observable.fromArray(itemBean)
            }.subscribe(object : IObserver<PostBackBean>() {
                override fun onNext(t: PostBackBean) {
                    liveData.postValue(t)
                }
            })

        return liveData
    }
}