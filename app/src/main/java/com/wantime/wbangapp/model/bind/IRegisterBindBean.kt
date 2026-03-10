package com.wantime.wbangapp.model.bind

import androidx.databinding.ObservableField

class IRegisterBindBean {
    var username: ObservableField<String> = ObservableField()
    var password: ObservableField<String> = ObservableField()
    var passwordAgin: ObservableField<String> = ObservableField()
    var email: ObservableField<String> = ObservableField()
    var inviteCode: ObservableField<String> = ObservableField()
    var verifyCode: ObservableField<String> = ObservableField()
}