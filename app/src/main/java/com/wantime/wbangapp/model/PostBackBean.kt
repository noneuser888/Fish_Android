package com.wantime.wbangapp.model

import com.wantime.wbangapp.utils.NetCode

class PostBackBean {
    var ok: Int = NetCode.NET_FAILED
    var message: String = ""
    var code: Int = 0
    var authOrderId: String = ""
    var redirect_url=""
}