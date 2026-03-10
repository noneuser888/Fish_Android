package com.wantime.wbangapp.model

import com.wantime.wbangapp.utils.Constants

class AccountBean {
    var Title=""
    var position=0

    var isSelect=false
    get() { return Constants.accountType==position}

}