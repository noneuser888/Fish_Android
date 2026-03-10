package com.wantime.wbangapp.request

class RetrofitHelper {
    companion object {
        private var mRetrofitHelper: RetrofitHelper? = null
        fun getInstance(): RetrofitHelper {
            if (mRetrofitHelper == null) {
                synchronized(RetrofitHelper::class.java) {
                    mRetrofitHelper = RetrofitHelper()
                }
            }
            return mRetrofitHelper!!
        }
    }


}