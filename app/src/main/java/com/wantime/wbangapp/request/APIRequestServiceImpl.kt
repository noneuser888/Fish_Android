package com.wantime.wbangapp.request

import android.annotation.SuppressLint
import android.text.TextUtils
import android.util.Log
import com.wantime.wbangapp.BuildConfig
import com.wantime.wbangapp.utils.Constants
import com.wantime.wbangapp.utils.NetCode
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


/**
 *  on 2018/1/19.
 * http://blog.csdn.net/u013802160/article/details/51860541
 */
class APIRequestServiceImpl private constructor() : APIRequestService {

    companion object {
        private const val HTTPGET: String = "GET"
        private const val HTTPPOST: String = "POST"
        private const val HTTPDEL: String = "DELETE"

        private var instance1: APIRequestServiceImpl? = null
        const val mCodeKeys = "Code"
        private const val accessTokenFront = "Bearer"

        //配置通用的数据
        private fun setCommonBaseParams(xBuilder: FormBody.Builder): FormBody.Builder {
            xBuilder.add("device", "tb_android")
            xBuilder.add("version", BuildConfig.VERSION_NAME)
            return xBuilder
        }

        //配置通用的头部
        private fun setCommonBaseHeaderParams(mBuilder: okhttp3.Request.Builder) {
            setCommonBaseHeaderParams(mBuilder, Constants.onGetUserBaseInfoWithToken().token)
        }


        private fun setCommonBaseHeaderParams(mBuilder: okhttp3.Request.Builder, access_token: String) {
            mBuilder.addHeader("device", "2")
            mBuilder.addHeader("accept", "application/json, text/plain, */*")
            mBuilder.addHeader("Content-type", "application/json,charset=UTF-8")
            mBuilder.addHeader("version", BuildConfig.VERSION_CODE.toString())
            if (!TextUtils.isEmpty(access_token)) mBuilder.addHeader("Authorization", accessTokenFront+ access_token)
        }

        //配置请求头部
        private fun birthRequestBody(params: JSONObject): RequestBody {
            val mJSON = "application/json;charset=utf-8".toMediaTypeOrNull()
            return RequestBody.create(mJSON, params.toString())
        }

        //表单模式传输数据
        private fun birthFormRequestBody(mBuilder: FormBody.Builder, args: JSONObject): FormBody.Builder {
            val argsIterator = args.keys().iterator();
            while (argsIterator.hasNext()) {
                val mKeys = argsIterator.next();
                mBuilder.add(mKeys, args.getString(mKeys));
            }
            return mBuilder
        }

        //登录传递数据
        private fun birthLoginRequestBody(mBuilder: FormBody.Builder, args: JSONObject): FormBody.Builder {
            val argsIterator = args.keys().iterator()
            while (argsIterator.hasNext()) {
                val mKeys = argsIterator.next()
                mBuilder.add(mKeys, args.getString(mKeys))
            }
            return mBuilder
        }

        /**
         * 暂时不用键值对的方式来传递数据 采用JSON直接交互
         * ***/
        private fun getFormBuilder(): FormBody.Builder {
            val mBuilder = FormBody.Builder()
            setCommonBaseParams(mBuilder)
            return mBuilder
        }

        @Throws(IOException::class)
        private fun synRequest(usrkeys: String, mRequest: Request): Response? {
            if (TextUtils.isEmpty(usrkeys)) throw IllegalArgumentException("usrkeys is not null！")
            val mOkHttpClient = OkHttpClientHelper.findOkHttpClientByKeys(usrkeys)
            return mOkHttpClient?.newCall(mRequest)?.execute()
        }

        @Throws(IOException::class)
        private fun asynRequest(usrkeys: String, mRequest: Request, xCallBack: Callback) {
            if (TextUtils.isEmpty(usrkeys)) throw IllegalArgumentException("usrkeys is not null！")
            val mOkHttpClient = OkHttpClientHelper.findOkHttpClientByKeys(usrkeys)
            mOkHttpClient?.newCall(mRequest)?.enqueue(xCallBack)
        }

        private fun getRequestUrl(api: String): String {
            if (Constants.isHttpLink(api)) return api
            return Constants.baseAPIUrl + api
        }

        private fun getRequest(addHeader: Boolean, method: String, api: String, mJson: JSONObject): Request {
            return getRequest(Constants.onGetUserBaseInfoWithToken().token, addHeader, method, api, mJson)
        }

        private fun getRequest(access_token: String, addHeader: Boolean, method: String, api: String, mJson: JSONObject): Request {
            val requestUrl: String = getRequestUrl(api)
            val mBuilder: Request.Builder = getRequest()
//            val acc="eyJhbGciOiJSUzI1NiIsImtpZCI6ImQ2MjlhMjgwMWEyNjg4YWZkNTM4MDU2ZjhiZmVkOGE3IiwidHlwIjoiSldUIn0.eyJuYmYiOjE1NjIzMjAxODAsImV4cCI6MTU2MjMzNDU4MCwiaXNzIjoiaHR0cDovL3d3dy5yeWpqLmN5Z2VycC5jb20iLCJhdWQiOlsiaHR0cDovL3d3dy5yeWpqLmN5Z2VycC5jb20vcmVzb3VyY2VzIiwiMWQxY2I3MTU4ZGZkZjY5NGFiOWU2ZjI1MWRkMTA4MTgiXSwiY2xpZW50X2lkIjoiYXBwc2VydmVyXzIwMDAxIiwic3ViIjoiNWQxZDY2NzVjNWQ2NTQwMDA2NDc1ZDAyIiwiYXV0aF90aW1lIjoxNTYyMzIwMTgwLCJpZHAiOiJsb2NhbCIsIklkIjoiNWQxZDY2NzVjNWQ2NTQwMDA2NDc1ZDAyIiwiRnVsbE5hbWUiOiLnv4HnjonmooUiLCJVc2VyTmFtZSI6IjE1ODA4Mzk4MDQ0IiwiQ29tbXBhbnlOYW1lIjoi5bm_5YWD5biC5Yip5bee5Yy66Im65bCa5bu65p2Q6JCl5Lia6YOoIiwiUm9sZXMiOiI1YzZmYjIzZTkzMmY5NTAwMDhlNzZkOWYsNWMxNzE3NWVkM2IyNzkwMDA4ZDhiNDNjLDVkMWM3NDBkNzIyNjVlMDAwNmM5ZWMzZiIsIlJvbGVOYW1lcyI6IuW5v-WFg-W4guWIqeW3nuWMuuiJuuWwmuW7uuadkOiQpeS4mumDqCzmiqXku7flkZgs6LSi5YqhIiwiT3JnYW5JZHMiOiIyMTRlYTU4MC1hOWVhLTQwZWItOWE5YS1hMzUxYTE2MGY2MzkiLCJMb2dpbklkIjoiNWQxZjFkMzQ2YzZiZTIwMDA2MDM3NDJhIiwic2NvcGUiOlsiMWQxY2I3MTU4ZGZkZjY5NGFiOWU2ZjI1MWRkMTA4MTgiXSwiYW1yIjpbInB3ZCJdfQ.mdj7zH_HcYibzSyGFNxQ3yQOFuzsfRp1D-PJSlVIPaJGym7alIheK7wPuOijBFzXHxLQHpC7cv4IUkzRRRq13ooFvsAN40_wsMRcWFgvlCweZvLhrc3jHmbMmPMNFi7voq9uTXgLW9Uy1X1N6J63XrKUS38hrCmzehV0lg3NhtIj-bcfaZWYNdGg6IgmIPFSAxPbprdTRblLPafbmEc0MjeYwifwUWQfmF8NVztM0iJyg7rj8sC_gmsAbnCcBj9VKpxIQmwDeLiR3Wuxh5ZI5OEO8FRfWC3PDxegi57QLgXjXhrThvGGAZJW4Gq3TdqWSHVtRm8mAEv78ulv3P5Zig"
            if (addHeader) setCommonBaseHeaderParams(mBuilder, access_token)
            when (method) {
                HTTPGET -> {
                    mBuilder.url(birthGetReQuestUrl(requestUrl, mJson))
                }
                HTTPPOST -> {
                    mBuilder.url(requestUrl)
                    if (addHeader)
                        mBuilder.post(birthRequestBody(mJson))
                    else {//表单模式
                        val formBuilder = FormBody.Builder()
                        mBuilder.post(birthFormRequestBody(formBuilder, mJson).build())
                    }
                }
                HTTPDEL -> {
                    mBuilder.url(requestUrl)
                    mBuilder.delete(birthRequestBody(mJson))
                }
            }
            return mBuilder.build()
        }


        private fun birthGetReQuestUrl(requestUrl: String, mJson: JSONObject): String {
            var mParam = ""
            val mIterator = mJson.keys()
            while (mIterator.hasNext()) {
                val mkey: String = mIterator.next()
                mParam += "&" + mkey + "=" + mJson.get(mkey)
            }
            return requestUrl + mParam
        }


        private fun birthGetReQuestUrl(requestUrl: String, mIterator: Iterator<String>?): String {
            var mParam: String = ""
            if (mIterator != null) {
                mParam = birthGetReQuestParams(mIterator)
            }
            return requestUrl + mParam
        }

        private fun birthGetReQuestParams(mIterator: Iterator<String>?): String {
            var mParam = ""
            if (mIterator != null)
                while (mIterator.hasNext()) {
                    val mItem: String = mIterator.next()
                    val first: Int = mItem.indexOf("=")
                    if (first < 1 || first == mItem.length - 1) continue
                    val mKeys: String = mItem.substring(0, first)
                    val mValue: String = mItem.substring(first + 1)
                    mParam += "&$mKeys=$mValue"
                }
            return mParam
        }

        private fun birthPostReQuestFormBd(mBuilder: FormBody.Builder, mIterator: Iterator<String>?) {
            if (mIterator != null) {
                birthPostRequestParams(mBuilder, mIterator)
            }
        }

        private fun birthPostReQuestFormBd(mBuilder: FormBody.Builder, mJson: JSONObject) {
            val mIterator = mJson.keys()
            while (mIterator.hasNext()) {
                val mkey: String = mIterator.next()
                mBuilder.add(mkey, mJson.getString(mkey))
            }
        }

        private fun birthPostRequestParams(mBuilder: FormBody.Builder, mIterator: Iterator<String>) {
            while (mIterator.hasNext()) {
                val mItem: String = mIterator.next()
                val first: Int = mItem.indexOf("=")
                if (first < 1 || first == mItem.length - 1) continue
                val mKeys: String = mItem.substring(0, first)
                val mValue: String = mItem.substring(first + 1)
                mBuilder.add(mKeys, mValue)
            }
        }

        private fun getRequest(): Request.Builder {
            return Request.Builder()
        }

        @Synchronized
        fun getInstance(): APIRequestServiceImpl {
            if (instance1 == null) {
                instance1 = APIRequestServiceImpl()
            }
            return instance1!!
        }
    }

    fun apiRequestNetLoginPost(api: String, args: JSONObject, xCallBack: ValueCallBack) {
        val mBuilder: okhttp3.Request.Builder = getRequest()
        mBuilder.addHeader("Content-type", "application/json;charset=UTF-8")
        mBuilder.url(api)
        mBuilder.post(birthLoginRequestBody(FormBody.Builder(), args).build())
        val mRequest = mBuilder.build()
        startObservable(mRequest, xCallBack)
    }

    override fun apiRequestNetPost(api: String, args: JSONObject, xCallBack: ValueCallBack) {
        requestNetService(true, HTTPPOST, api, args, xCallBack)
    }

    override fun apiRequestNetGet(api: String, args: JSONObject, xCallBack: ValueCallBack) {
        requestNetService(true, HTTPGET, api, args, xCallBack)
    }

    override fun apiRequestNetDel(api: String, args: JSONObject, xCallBack: ValueCallBack) {
        requestNetService(true, HTTPDEL, api, args, xCallBack)
    }

    override fun apiRequestNetPostNoHeader(api: String, args: JSONObject, xCallBack: ValueCallBack) {
        requestNetService(false, HTTPPOST, api, args, xCallBack)
    }

    //自带access_token
    open fun apiRequestNetGet(access_token: String, api: String, args: JSONObject, xCallBack: ValueCallBack) {
        requestNetService(access_token, true, HTTPGET, api, args, xCallBack)
    }

    //同步支持方案
    override fun apiRequestNetDelSync(api: String, args: JSONObject): JSONObject {
        return requestNetServiceSync(true, HTTPDEL, api, args)
    }

    override fun apiRequestNetGetSync(api: String, args: JSONObject): JSONObject {
        return requestNetServiceSync(true, HTTPGET, api, args)
    }

    override fun apiRequestNetPostSync(api: String, args: JSONObject): JSONObject {
        return requestNetServiceSync(true, HTTPPOST, api, args)
    }

    override fun apiRequestNetPostSyncNoHeader(api: String, args: JSONObject): JSONObject {
        return requestNetServiceSync(false, HTTPPOST, api, args)
    }

    ///增加通用同步数据,该函数为请求其他网页数据
    fun apiRequestNetGetSyncForCommon(apiPath: String): String {
        val mBuilder: okhttp3.Request.Builder = getRequest()
        mBuilder.url(apiPath)
//        mBuilder.addHeader("device", "2")
//        mBuilder.addHeader("accept", "application/json, text/plain, */*")
//        mBuilder.addHeader("Content-type", "application/json,charset=UTF-8")
//        mBuilder.addHeader("version", BuildConfig.VERSION_CODE.toString())
        val mResponse=synRequest("Common", mBuilder.build())
        try{
            return mResponse!!.body!!.string()
        }catch (ex:java.lang.Exception){}
        return ""
    }


//    fun apiRequestNetPostSyncByteArray(args: JSONObject): JSONObject {
//        var apiPath = "http://192.168.3.5:10000/api/" + APIRequestPath.API_PATH_POST_SERVICE
//        return requestNetServiceSyncByteArray(HTTPPOST, apiPath, args)
//    }

    override fun apiRequestMultipart(apiPath: String, multipartBody: MultipartBody, xCallBack: okhttp3.Callback) {
        val mBuilder: okhttp3.Request.Builder = getRequest()
        mBuilder.url(apiPath)
        mBuilder.post(multipartBody)
        setCommonBaseHeaderParams(mBuilder)
        asynRequest(Constants.getUserKeys(), mBuilder.build(), xCallBack)
    }

    override fun apiRequestMultipartSync(apiPath: String, multipartBody: MultipartBody):JSONObject {
        val requestUrl: String = getRequestUrl(apiPath)
        val mBuilder: okhttp3.Request.Builder = getRequest()
        mBuilder.url(requestUrl)
        mBuilder.post(multipartBody)
        setCommonBaseHeaderParams(mBuilder)
        var resultJson: JSONObject? = null
        resultJson = try {
            val mResponse = synRequest(Constants.getUserKeys(), mBuilder.build())
            dealResponse(mResponse)
        } catch (ex: Exception) {
            dealResponseError(ex)
        }

        return resultJson
    }

    //IM 单独接口
    open fun apiRequestMultipartForIm(apiPath: String, multipartBody: MultipartBody, access_token: String, xCallBack: okhttp3.Callback) {
        val mBuilder: okhttp3.Request.Builder = getRequest()
        mBuilder.url(apiPath)
        mBuilder.post(multipartBody)
        setCommonBaseHeaderParams(mBuilder, access_token)
        asynRequest(Constants.getUserKeys(), mBuilder.build(), xCallBack)
    }


    private fun requestNetService(addHeader: Boolean, httpMethod: String, api: String, args: JSONObject, xCallBack: ValueCallBack) {
        requestNetService(getToken(), addHeader, httpMethod, api, args, xCallBack)
    }

    private fun requestNetService(access_token: String, addHeader: Boolean, httpMethod: String, api: String, args: JSONObject, xCallBack: ValueCallBack) {
        val mRequest = getRequest(access_token, addHeader, httpMethod, api, args)
        startObservable(mRequest, xCallBack)
    }

    private fun requestNetServiceSync(addHeader: Boolean, httpMethod: String, api: String, args: JSONObject): JSONObject {
        val mRequest = getRequest(addHeader, httpMethod, api, args)
        var resultJson: JSONObject? = null
        resultJson = try {
            val mResponse = synRequest(Constants.getUserKeys(), mRequest)
            dealResponse(mResponse)
        } catch (ex: Exception) {
            dealResponseError(ex)
        }

        return resultJson
    }

    //https://blog.csdn.net/u011418943/article/details/55667115
//    private fun requestNetServiceSyncByteArray(httpMethod: String, api: String, args: JSONObject): JSONObject {
//        var mRequest = getRequest(httpMethod, api, args)
//        var resultJson: JSONObject? = null
//        try {
//            var mResponse = synRequest(Constants.getUserKeys(), mRequest)
//
//            var buffer = mResponse?.body()?.bytes()
//
//            //计算长度原始的数据产长度
//            if (buffer != null && buffer.isNotEmpty()) {
//                //前面几位是长度
//                val srcLength = Lz4Utils.countLength(buffer)
//                if (srcLength > 0) {
//                    var decompressBuffer = Lz4Util.decompressorByte(buffer)
//                    Lz4Util.wrapToMsgPack(decompressBuffer)
//                }
//            }
//        } catch (ex: Exception) {
//            resultJson = dealResponseError(ex)
//        }
//
//        return resultJson!!
//    }

    //POST 或者DELETE 处理数据
    @SuppressLint("CheckResult")
    private fun startObservable(mRequest: Request, xCallBack: ValueCallBack) {
        val mObservable = Observable.create(ObservableOnSubscribe<Any> { emitter ->
            try {
                val mResponse = synRequest(Constants.getUserKeys(), mRequest)
                val mJson = dealResponse(mResponse)
                emitter.onNext(mJson)
                emitter.onComplete()
            } catch (ex: Exception) {
                emitter.onError(ex)
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        mObservable.subscribe(
                { mAny -> xCallBack.onValueBack(mAny) },
                { mAny -> xCallBack.onValueBack(dealResponseError(mAny)) },
                {
                    //完成
                })
    }

    //统一处理响应
    fun dealResponse(mResponse: Response?): JSONObject {

        var resultJson: JSONObject? = null

        if (mResponse != null) {
            val content = mResponse.body!!.string()
            val message = mResponse.message
            resultJson = if (TextUtils.isEmpty(content)) dealResponseError(message)
            else try {
                JSONObject(content)
            } catch (ex: JSONException) {
                dealResponseError(content)
            }


        }
        if (resultJson == null) resultJson = dealResponseError("unknowns error!")
        //有些地方的code 为小写
        if (resultJson.has("code")) resultJson.put(mCodeKeys, resultJson.get("code"))
        if (!resultJson.has(mCodeKeys)) resultJson.put(mCodeKeys, NetCode.NET_SUCCESS)
        if (!resultJson.has("message") && resultJson.has("error_description")) resultJson.put("message", resultJson.getString("error_description"))

        //输出错误的数据
        if (resultJson.has(mCodeKeys) && resultJson.getInt(mCodeKeys) == NetCode.NET_FAILED) {

        }

        return resultJson
    }


    private fun dealResponseError(ex: Any): JSONObject {
        val resultJson = JSONObject()
        resultJson.put(mCodeKeys, NetCode.NET_FAILED)
        resultJson.put("message", ex.toString())
        resultJson.put("Msg", ex.toString())
        return resultJson
    }

    private fun dealOkResponse(message: String): JSONObject {
        val resultJson = JSONObject()
        resultJson.put(mCodeKeys, NetCode.NET_SUCCESS)
        resultJson.put("message", message)
        resultJson.put("Msg", message)
        return resultJson
    }


    private fun getToken():String {
        return Constants.onGetUserBaseInfoWithToken().token
    }



}