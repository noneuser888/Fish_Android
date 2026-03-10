package com.wantime.wbangapp.model.bind

import androidx.databinding.ObservableField

class IPlatformBindBean {
    var platformName: ObservableField<String> = ObservableField<String>()
    var appid: ObservableField<String> = ObservableField<String>()
    var bundleid: ObservableField<String> = ObservableField<String>()
    var scope: ObservableField<String> = ObservableField<String>()
    var type: ObservableField<String> = ObservableField<String>()
    var wxNickname: ObservableField<String> = ObservableField<String>()
    var imageUrl: ObservableField<String> = ObservableField<String>()
    var description: ObservableField<String> = ObservableField<String>()
    var contact: ObservableField<String> = ObservableField<String>()
    var device: ObservableField<String> = ObservableField<String>()

    init {
        type.set("1")
        device.set("2")
        description.set("")
    }
}