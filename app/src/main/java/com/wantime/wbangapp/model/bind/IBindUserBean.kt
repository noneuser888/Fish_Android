package com.wantime.wbangapp.model.bind

import androidx.databinding.BaseObservable
import androidx.databinding.ObservableField

//所有需要双向绑定的
class IBindUserBean: BaseObservable() {
    var account: ObservableField<String> = ObservableField<String>()
    var password: ObservableField<String> = ObservableField<String>()
    var phone: ObservableField<String> = ObservableField<String>()
}