package com.wantime.wbangapp.viewmodel

import android.app.Activity
import android.app.Application
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSON
import com.wantime.wbangapp.common.BaseViewModel
import com.wantime.wbangapp.inter.IObserver
import com.wantime.wbangapp.model.AppealRecordBean
import com.wantime.wbangapp.model.PostBackBean
import com.wantime.wbangapp.model.ZSRepresentBean
import com.wantime.wbangapp.request.RetrofitManager
import com.wantime.wbangapp.utils.Constants
import com.wantime.wbangapp.utils.NetCode
import droidninja.filepicker.utils.ContentUriUtils
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class AppealViewModel(application: Application) : BaseViewModel(application) {
    private val pageSize: Int = 20 //每页主要20条
    private var mTotalPage = 1000000
    private var currentPage = 1 //当前分页

    fun onRefreshNews(): MutableLiveData<AppealRecordBean> {
        currentPage = 1
        return onGetAppealRecord(1)
    }

    fun onLoadMore(): MutableLiveData<AppealRecordBean> {
        return onGetAppealRecord(++currentPage)
    }

    private fun onGetAppealRecord(page: Int): MutableLiveData<AppealRecordBean> {
        val liveData: MutableLiveData<AppealRecordBean> = MutableLiveData<AppealRecordBean>()
        RetrofitManager.getInstance().apiRequest.onRepresentRecord(page, pageSize)
            .subscribeOn(Schedulers.io()).flatMap {
                var iBean: AppealRecordBean? = null
                val mContent = it
                Constants.onGetInfo(mContent).apply {
                    when (optInt("code")) {
                        Constants.NET_OK -> {
                            iBean =
                                JSON.parseObject(getString("data"), AppealRecordBean::class.java)
                            mTotalPage = iBean?.pages!!
                        }
                    }
                }
                if (iBean == null) iBean = AppealRecordBean()
                Observable.fromArray(iBean!!)
            }.subscribe(object : IObserver<AppealRecordBean>() {
                override fun onNext(t: AppealRecordBean) {
                    liveData.postValue(t)
                }
            })

        return liveData
    }


    fun onAppealFormPost(mActivity: Activity, taskID: String, taskReason: String, taskType: String, photoList: ArrayList<Uri>):MutableLiveData<PostBackBean> {
        val liveData: MutableLiveData<PostBackBean> = MutableLiveData<PostBackBean>()
        val itemBean = ZSRepresentBean()
        itemBean.authType = taskType
        itemBean.authOrderId = taskID
        itemBean.description = taskReason

        if (photoList.size > 0) {
            val multipartBodyBuilder = MultipartBody.Builder()
            multipartBodyBuilder.setType(MultipartBody.FORM)

            for (imagePath in photoList) {
                val file = File(ContentUriUtils.getFilePath(mActivity, imagePath)!!)
                val requestFile: RequestBody =
                    RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                multipartBodyBuilder.addFormDataPart("files", file.name, requestFile)
            }

            RetrofitManager.getInstance().apiRequest.uploadPicMore(multipartBodyBuilder)
                .subscribeOn(Schedulers.io()).subscribe(object : IObserver<String>() {
                    override fun onNext(t: String) {
                        Constants.onGetInfo(t).apply {
                            when (optInt("code")) {
                                Constants.NET_OK -> {
                                    itemBean.descImages = getString("data")
                                    onPostAppealInfo(itemBean,liveData)
                                }
                            }
                        }
                    }

                    override fun onError(e: Throwable) {
                        onPostAppealInfo(itemBean,liveData)
                    }
                })
        } else {
            onPostAppealInfo(itemBean,liveData)
        }
        return liveData
    }

    //申请
    private fun onPostAppealInfo(iBean: ZSRepresentBean, liveData: MutableLiveData<PostBackBean>): MutableLiveData<PostBackBean> {
        RetrofitManager.getInstance().apiRequest.onZSRepresent(iBean).subscribeOn(Schedulers.io())
            .flatMap {
                val itemBean = PostBackBean()
                val mContent = it
                Constants.onGetInfo(mContent).apply {
                    when (optInt("code")) {
                        Constants.NET_OK -> {
                            itemBean.ok = NetCode.NET_SUCCESS
                        }
                    }
                    itemBean.message=optString("message")
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