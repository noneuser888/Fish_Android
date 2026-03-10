package com.wantime.wbangapp.model

class ProcessInfo(
    name: String?,
    pid: Int?,
    isSys: Boolean,
    processName: String?
) {
    companion object {
        private var name: String? = null
        private var pid: Int? = 0
        private var isSys = false
        private var processName: String? = null
    }

    init {
        Companion.name = name
        Companion.pid = pid
        Companion.isSys = isSys
        Companion.processName = processName
    }
}