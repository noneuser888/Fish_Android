package com.wantime.wbangapp.model

import android.text.TextUtils

open class UserBean {
    var balance: Double = 0.0
    var qrCodeBalance: Double = 0.0
    var agencyId: String = ""
    var id: String = ""
    var token: String = ""
    var username: String = ""
    var account: String = ""
    var email: String = ""
    var password: String = ""
    var phone: String = ""
    var level: String = ""
    var inviteUrl: String? = ""
    var nickname: String? = ""
        get() {
            return if (TextUtils.isEmpty(field)) "昵称 （未设置）"
            else "昵称 $field"
        }
    var exclusiveBalance: Double = 0.0
    var status: Int = 0

    /*********其他信息***********/
    var userType = ""
    var inviteCode = ""
    var myAgency=""
    var myAgencyNickname=""

    open fun getUserLevel(): String {
        return if (!TextUtils.isEmpty(level))
            when (level) {
                "1" -> "一级代理"
                "2" -> "二级代理"
                else -> "普通用户"
            } else when (userType) {
            "1" -> "一级代理"
            "2" -> "二级代理"
            else -> "普通用户"
        }
    }


}