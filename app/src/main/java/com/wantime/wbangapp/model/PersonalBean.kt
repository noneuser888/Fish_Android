package com.wantime.wbangapp.model

class PersonalBean {
    var type: Int = 0
    var title: String = ""
    var icon: Int = 0
    var iOption: Int = -1


    enum class Options {
        authorityList,
        myTask,
        wbClass,
        myEmail,
        myShenSu,
        existLogin,
    }
}