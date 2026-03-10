package com.wantime.wbangapp.request.sslcert

import java.io.InputStream
import okio.Buffer
import java.io.IOException
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.util.*
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

/**
 *  on 2018/1/18.
 * 专门配置移动证书的类
 * 使用方法请将SSL移动证书串定义拷贝添加
 */
class CustomSSLCert {

    /**
     * 设置证书 并转换为输入流
     *
     * @return
     */
    fun trustedCertificatesInputStream(): InputStream {
        //服务器证书
        var serverAddressCa101 ="-----BEGIN CERTIFICATE-----\n" +
                "MIIDkTCCAnmgAwIBAgIJAIyqa0kaQk19MA0GCSqGSIb3DQEBCwUAMF8xCzAJBgNV\n" +
                "BAYTAkFVMRMwEQYDVQQIDApTb21lLVN0YXRlMSEwHwYDVQQKDBhJbnRlcm5ldCBX\n" +
                "aWRnaXRzIFB0eSBMdGQxGDAWBgNVBAMMDzEwMS4yMDEuMTIxLjEyNjAeFw0xNjEx\n" +
                "MTAxMjAzNTNaFw0yNjExMDgxMjAzNTNaMF8xCzAJBgNVBAYTAkFVMRMwEQYDVQQI\n" +
                "DApTb21lLVN0YXRlMSEwHwYDVQQKDBhJbnRlcm5ldCBXaWRnaXRzIFB0eSBMdGQx\n" +
                "GDAWBgNVBAMMDzEwMS4yMDEuMTIxLjEyNjCCASIwDQYJKoZIhvcNAQEBBQADggEP\n" +
                "ADCCAQoCggEBAKy/h1ObTc4yqh2aeWhhoKHzjBJQ+AaMe2vCa3K7mYfvri1J2J9K\n" +
                "PXFFZHoq8Yy0OAIejON/PeCsAEt2LDzop9yqEcVN9umhg4EX519c2HDTMym5MCZD\n" +
                "doqagA7/AfWojoocnb0RjHnxGTB9oOtqH9DszFf+DYs2Wr3ZS/DLC/APKr1YjDnq\n" +
                "yBTuG1zJfxmaYxxpLaN2f3FoSrSSrDJ1dGYpUZmmosn+6JgLL5Au0sMplTpeY8iD\n" +
                "ytGJfx7XPVSBrl5PtT1+V9LdRFW+tnAPgB4WUFtPRaWJT0capdlYVSyU9utMN12F\n" +
                "J9UgPbyySAePElwi4zdHh0MtK1MNu8bCg4MCAwEAAaNQME4wHQYDVR0OBBYEFMOF\n" +
                "MeGv+xpiDsgxjClYOgbNwzm7MB8GA1UdIwQYMBaAFMOFMeGv+xpiDsgxjClYOgbN\n" +
                "wzm7MAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQELBQADggEBAI1vT2GUvYtcJ/nj\n" +
                "+7rfQpUbVdtCEf276OJ1aU9Xqarf84sHrhoMl/2YX9IXXupOUDcpfWO6PkW+ncyN\n" +
                "ChETLOdHLfkF9pwDot51x8taElk87hUjOiwEr3Mn9cIhbNOzQIfB7xPAtYX1tnHV\n" +
                "+jBL0IvjvDP4NjqorAkEgakchN7THdueCrdVjT9bzCOuBHoGFLL+5QV5ZFGHT6uG\n" +
                "/dTRPp5wpKfcU2c3u8wodtDs22HuzF6VviuVMDQzr64nOEmiYn9LdDebVIUYIKIN\n" +
                "ZDSW/Eiu+FGOWQ8VraZERfI2VsTBu9QR36Mu7YOAYr0I2EvAotBfDpMFh9qhhk+O\n" +
                "dY9896w=\n" +
                "-----END CERTIFICATE-----\n"

        return Buffer()
                .writeUtf8(serverAddressCa101)
                .inputStream()
    }

    /**
     * Returns a trust manager that trusts `certificates` and none other. HTTPS services whose
     * certificates have not been signed by these certificates will fail with a `SSLHandshakeException`.
     *
     *
     *
     * This can be used to replace the host platform's built-in trusted certificates with a custom
     * set. This is useful in development where certificate authority-trusted certificates aren't
     * available. Or in production, to avoid reliance on third-party certificate authorities.
     *
     *
     *
     * See also [CertificatePinner], which can limit trusted certificates while still using
     * the host platform's built-in trust store.
     *
     *
     * <h3>Warning: Customizing Trusted Certificates is Dangerous!</h3>
     *
     *
     *
     * Relying on your own trusted certificates limits your server team's ability to update their
     * TLS certificates. By installing a specific set of trusted certificates, you take on additional
     * operational complexity and limit your ability to migrate between certificate authorities. Do
     * not use custom trusted certificates in production without the blessing of your server's TLS
     * administrator.
     */
    @Throws(GeneralSecurityException::class)
    fun trustManagerForCertificates(`in`: InputStream): X509TrustManager {
        // CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");//之前的代码，最好加上别名防止某些证书无法使用
        val certificateFactory = CertificateFactory.getInstance("X.509", "BC")
        val certificates = certificateFactory.generateCertificates(`in`)
        if (certificates.isEmpty()) {
            throw IllegalArgumentException("expected non-empty set of trusted certificates")
        }

        // Put the certificates a key store.
        val password = "password".toCharArray() // Any password will work.
        val keyStore = newEmptyKeyStore(password)
        var index = 0
        for (certificate in certificates) {
            val certificateAlias = Integer.toString(index++)
            keyStore.setCertificateEntry(certificateAlias, certificate)
        }

        // Use it to build an X509 trust manager.
        val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        keyManagerFactory.init(keyStore, password)
        val trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(keyStore)
        val trustManagers = trustManagerFactory.getTrustManagers()
        if (trustManagers.size != 1 || trustManagers[0] !is X509TrustManager) {
            throw IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers))
        }
        return trustManagers[0] as X509TrustManager
    }
    @Throws(GeneralSecurityException::class)
    fun newEmptyKeyStore(password: CharArray): KeyStore {
        try {
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            val `inputStream`: InputStream? = null // By convention, 'null' creates an empty key store.
            keyStore.load(`inputStream`, password)
            return keyStore
        } catch (e: IOException) {
            throw AssertionError(e)
        }

    }
}