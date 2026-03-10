package com.wantime.wbangapp.inter

import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import okhttp3.ResponseBody

abstract  class IObserver<T>: Observer<T> {
    override fun onComplete() {

    }

    override fun onError(e: Throwable) {

    }

    override fun onNext(t: T) {

    }

    override fun onSubscribe(d: Disposable) {

    }
}