package com.wantime.wbangapp.request

import okhttp3.MultipartBody
import org.json.JSONObject

/**
 *  on 2018/1/19.
 */
interface APIRequestService {

    //请求传输数据的方式是JSON 采用异步方案
    fun apiRequestNetPost(api: String, args: JSONObject, xCallBack: ValueCallBack)

    fun apiRequestNetGet(api: String, args: JSONObject, xCallBack: ValueCallBack)
    fun apiRequestNetDel(api: String, args: JSONObject, xCallBack: ValueCallBack)

    /***
     * 采用同步方案
     * ***/

    fun apiRequestNetPostSync(api: String, args: JSONObject): JSONObject

    fun apiRequestNetGetSync(api: String, args: JSONObject): JSONObject
    fun apiRequestNetDelSync(api: String, args: JSONObject): JSONObject

    /***
     * 不共用request header方案
     * ***/
    fun apiRequestNetPostNoHeader(api: String, args: JSONObject, xCallBack: ValueCallBack)

    fun apiRequestNetPostSyncNoHeader(api: String, args: JSONObject): JSONObject
    /***
     * 富媒体文数据处理 https://blog.csdn.net/moniteryao/article/details/52700934
     * https://blog.csdn.net/smallredzi/article/details/95477626 //采用这个？
     * **/
    fun apiRequestMultipart(api: String, mBody: MultipartBody, xCallBack: okhttp3.Callback)
    fun apiRequestMultipartSync(api: String, mBody: MultipartBody):JSONObject
}