package com.wantime.wbangapp.utils

object WxWxapiUtils {
    //_wxapi_command_type
    fun onGetWxapiCommandType(): String {
        val mBuffer = StringBuffer()
        mBuffer.append("_wx").append("api_").append("comm")
            .append("and_t").append("ype")
        return mBuffer.toString()
    }
    //_wxapi_basereq_openid
    fun onGetWxapiBasereqOpenId(): String {
        val mBuffer = StringBuffer()
        mBuffer.append("_wx").append("api_").append("bas")
            .append("ereq_ope").append("nid")
        return mBuffer.toString()
    }

    //_wxapi_sendauth_req_scope
    fun onGetWxapiSendAuthReqScope(): String {
        val mBuffer = StringBuffer()
        mBuffer.append("_wx").append("api_").append("_senda")
            .append("uth_req").append("_scope")
        return mBuffer.toString()
    }

    //_wxapi_sendauth_req_state
    fun onGetWxapiSendAuthReqstate(): String {
        val mBuffer = StringBuffer()
        mBuffer.append("_wx").append("api_").append("_senda")
            .append("uth_req").append("_state")
        return mBuffer.toString()
    }

    //_wxapi_basereq_transaction
    fun onGetWxapiSendAuthReqTransaction(): String {
        val mBuffer = StringBuffer()
        mBuffer.append("_wx").append("api_").append("bas")
            .append("ereq_tran").append("saction")
        return mBuffer.toString()
    }

    //_wxapi_basereq_openid
    fun onGetWxapiBasereqOpenid(): String {
        val mBuffer = StringBuffer()
        mBuffer.append("_wx").append("api_").append("bas")
            .append("ereq_op").append("enid")
        return mBuffer.toString()
    }
}