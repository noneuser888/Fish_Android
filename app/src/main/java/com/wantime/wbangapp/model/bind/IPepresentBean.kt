package com.wantime.wbangapp.model.bind

import androidx.databinding.BaseObservable
import androidx.databinding.ObservableField

//所有需要双向绑定的
class IPepresentBean: BaseObservable() {
    var authOrderId: ObservableField<String> = ObservableField<String>()
    var authType: ObservableField<String> = ObservableField<String>()
    var description: ObservableField<String> = ObservableField<String>()
    var descImages: ObservableField<String> = ObservableField<String>()
}