package com.wantime.wbangapp.utils

import java.util.*

class ITimer private constructor() {
    private var delay: Long = 0L
    private var period: Long = 0L
    private var xTimer: Timer? = null

    init {
        createTimer()
    }

    companion object {
        fun getTimer(): ITimer {
            return ITimer()
        }
    }

    private fun createTimer() {
        if (xTimer == null) {
            xTimer = Timer()
        }
    }

    fun onStart(runnable: Runnable): ITimer {
        xTimer!!.schedule(object : TimerTask() {
            override fun run() {
                runnable.run()
            }
        }, delay, period)
        return this
    }

    fun onDelay(delay: Long) :ITimer{
        this.delay = delay
        return this
    }

    fun onPeriod(period: Long):ITimer {
        this.period = period
        return this
    }

    fun stop() {
        if (xTimer != null) {
            xTimer!!.cancel()
        }
        xTimer = null
    }
}