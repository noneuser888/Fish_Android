package com.wantime.wbangapp.request

import com.wantime.wbangapp.request.sslcert.SSLTrustUtils
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import java.util.ArrayList
import java.util.HashMap
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

/**
 *  on 2018/1/18.
 */
object OkHttpClientHelper {
    //保存每个用户连接请求
    private val okHttpClients = HashMap<String, OkHttpClient>()
    private val TAG = "OkHttpClientHelper"
    private val RWTIME = 60*5
    private val RWTIME1 = 60*5

    fun findOkHttpClientByKeys(usrKeys: String): OkHttpClient? {
        var mxOkHttpClient: OkHttpClient? = null
        if (okHttpClients.containsKey(usrKeys)) {
            mxOkHttpClient = okHttpClients.get(usrKeys)
        } else {
            mxOkHttpClient = getOkHttpClient()
            okHttpClients.put(usrKeys, mxOkHttpClient)
        }
        return mxOkHttpClient
    }


    private fun getOkHttpClient(): OkHttpClient {
        val mBuilder = OkHttpClient.Builder().cookieJar(object : CookieJar {
            private val cookieStore = HashMap<String, List<Cookie>>()

            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                val mHost=url.host
                cookieStore[mHost] = cookies
            }

            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                val cookies = cookieStore[url.host]
                return cookies ?: ArrayList()
            }
        })
        //ssl
        if (SSLTrustUtils.getSSLSocketFactory() != null && SSLTrustUtils.getX509TrustManager() != null) {
            mBuilder.sslSocketFactory(
                SSLTrustUtils.getSSLSocketFactory()!!,
                SSLTrustUtils.getX509TrustManager()!!
            )
        }
        return mBuilder.hostnameVerifier(object : HostnameVerifier {
            override fun verify(p0: String?, p1: SSLSession?): Boolean {
                return true
            }

        })
            .connectTimeout(RWTIME.toLong(), TimeUnit.SECONDS)
            .writeTimeout(RWTIME1.toLong(), TimeUnit.SECONDS)
            .readTimeout(RWTIME1.toLong(), TimeUnit.SECONDS)
            .build()
    }
}