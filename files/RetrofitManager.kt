package com.wantime.wbangapp.request

import com.wantime.wbangapp.BuildConfig
import com.wantime.wbangapp.utils.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class RetrofitManager private constructor() {

    private var okHttpClient: OkHttpClient? = null
    private var retrofit: Retrofit? = null
    private var retrofitApiService: RetrofitApiService? = null
    private val accessTokenFront = "Bearer" //accessToken前面的部分

    init {
        initOkHttpClient()
        initRetrofit()
    }

    private fun initRetrofit() {
        retrofit = Retrofit.Builder()
            .baseUrl(Constants.baseAPIUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient!!)
            .build()
        retrofitApiService = retrofit!!.create(RetrofitApiService::class.java)
    }

    private fun initOkHttpClient() {
        okHttpClient = OkHttpClient.Builder() //设置缓存文件路径，和文件大小
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(object : Interceptor {
                //添加统一的拦截器
                override fun intercept(chain: Interceptor.Chain): Response {
                    val request: Request = chain.request()
                    val builder1: Request.Builder = request.newBuilder()
                    builder1.addHeader("type", "android")
                        .addHeader("version_Name", BuildConfig.VERSION_NAME)
                        .addHeader("version_Code", BuildConfig.VERSION_CODE.toString())
                    val mUserBean = Constants.onGetUserBaseInfoWithToken()
                    if (mUserBean.token.isNotEmpty()) {
                        builder1.addHeader("Authorization", accessTokenFront + mUserBean.token)
                    }
                    val build: Request = builder1.build()
                    return chain.proceed(build)
                }

            }).build()

    }

    companion object {
        private var retrofitManager: RetrofitManager? = null
        fun getInstance(): RetrofitManager {
            if (retrofitManager == null) {
                synchronized(RetrofitManager::class.java) {
                    if (retrofitManager == null) {
                        retrofitManager = RetrofitManager()
                    }
                }
            }
            return retrofitManager!!
        }


    }

    val apiService: RetrofitApiService get() = retrofitManager!!.retrofitApiService!!


}