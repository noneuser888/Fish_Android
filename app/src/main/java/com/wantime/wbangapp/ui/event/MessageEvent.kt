package com.wantime.wbangapp.ui.event

class MessageEvent {
    var mType: Int = 0
    var dataJson = ""

    enum class EventType {
        refreshTab,//刷新tab
        refreshFragment,//刷新fragment
        reaptScan,//重新扫描
        socket,//套接字类型
        appParam,//应用信息
        vertifyCheck,//实名认证
        requestAgainBaseInfo,//重新请求基本信息
        refreshUserInfo,//刷新头部用户信息
        loginException,//登陆状态异常
        closeMain,
    }
}