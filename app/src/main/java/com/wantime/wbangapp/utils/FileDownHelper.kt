package com.wantime.wbangapp.utils

import android.os.AsyncTask
import android.text.TextUtils
import com.wantime.wbangapp.request.ValueCallBack
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL


class FileDownHelper {

    companion object {
        private var mFileDownHelper: FileDownHelper? = null
        fun getInstance(): FileDownHelper {
            synchronized(FileDownHelper::class.java) {
                mFileDownHelper = FileDownHelper()
            }
            return mFileDownHelper!!
        }
    }

    fun onDownLoad(fileName: String, webUrl: String, valueCallBack: ValueCallBack) {
        DownLoader().down(fileName, webUrl, valueCallBack).execute()
    }

    fun onGetFileName(downLoadUrl: String): String {
        var fileName = ""
        if (!TextUtils.isEmpty(downLoadUrl))
            fileName =
                downLoadUrl.substring(downLoadUrl.lastIndexOf("/") + 1, downLoadUrl.length) + ".png"

        return fileName
    }

    private class DownLoader : AsyncTask<String?, Void?, String>() {
        private var downLoadUrl = ""
        private var fileName = ""
        private var valueCallBack: ValueCallBack? = null
        fun down(fileName: String, webUrl: String, valueCallBack: ValueCallBack): DownLoader {
            this.downLoadUrl = webUrl
            this.fileName = fileName
            return this
        }

        override fun doInBackground(vararg p0: String?): String {
            return onExeDownLoadFile()
        }

        private fun onExeDownLoadFile(): String {
            if (!TextUtils.isEmpty(downLoadUrl) && Constants.isHttpLink(downLoadUrl)) {
                val file = File(Constants.filePath + fileName)
                val url = URL(downLoadUrl)
                val conn = url.openConnection() as HttpURLConnection
                if (!file.exists()) {
                    file.createNewFile()
                    try {
                        var inputStream: InputStream? = null
                        conn.requestMethod = "GET"
                        conn.connectTimeout = 20000
                        if (conn.responseCode == 200) inputStream = conn.inputStream
                        val buffer = ByteArray(4096)
                        var len = 0
                        val outStream = FileOutputStream(file)
                        if (inputStream != null) {
                            while (inputStream.read(buffer).also { len = it } != -1) {
                                outStream.write(buffer, 0, len)
                            }
                        }
                        outStream.close()
                        valueCallBack?.onValueBack(fileName)
                    } catch (ex: Exception) {
                        if (file.exists()) file.delete()
                    }

                }
            }
            return fileName
        }
    }
}