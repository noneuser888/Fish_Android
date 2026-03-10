package com.wantime.wbangapp.model

class MenuPopupBean {
    var name = ""
    var option = 0
    var type:MenuType=MenuType.MAIN_AllUser

    enum class MenuType{
        MENU_NULL,
        MAIN_AllUser,//主页一级代理
        Agent_PrimaryAgent,//代理页面 一级代理
        User_PrimaryAgent,//用户 一级代理
        User_SecondaryAgent,//用户 二级代理
    }
}