package com.wantime.wbangapp.utils

object WxMessageUtils {
    //_mmessage_appPackage
    fun onGetAppPackage(): String {
        val mBuffer = StringBuffer()
        mBuffer.append("_mmess").append("age").append("_ap")
            .append("p").append("Pac").append("kage")

        return mBuffer.toString()
    }

    ///_mmessage_content
    fun onGetMessageContent(): String {
        val mBuffer = StringBuffer()
        mBuffer.append("_mmess").append("age").append("_con")
            .append("tent")

        return mBuffer.toString()
    }

    ///_message_token
    fun onGetMessageToken(): String {
        val mBuffer = StringBuffer()
        mBuffer.append("_mess").append("age").append("_tok")
            .append("en")

        return mBuffer.toString()
    }

    ///_mmessage_checksum
    fun onGetMessageChecksum(): String {
        val mBuffer = StringBuffer()
        mBuffer.append("_mmess").append("age").append("_check")
            .append("sum")

        return mBuffer.toString()
    }
}