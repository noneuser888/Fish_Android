package com.wantime.wbangapp.request.sslcert

import android.util.Log
import java.security.GeneralSecurityException
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 *  on 2018/1/18.
 * SSL Manager 证书链管理工具
 */
object SSLTrustUtils {
    private var trustManager: X509TrustManager? = null
    private var sslSocketFactory: SSLSocketFactory? = null
    /**
     * 初始化证书管理器
     * **/
    fun initSSLTrustManager() {
        var custom: CustomSSLCert = CustomSSLCert();
        try {
            trustManager = custom.trustManagerForCertificates(custom.trustedCertificatesInputStream())
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, arrayOf<TrustManager>(trustManager!!), java.security.SecureRandom())
            sslSocketFactory = sslContext.socketFactory
        } catch (e: GeneralSecurityException) {
            Log.e("GeneralSecurity", e.toString())
        }

    }

    fun getSSLSocketFactory(): SSLSocketFactory? {
        return sslSocketFactory
    }

    fun getX509TrustManager(): X509TrustManager? {
        return trustManager
    }
}