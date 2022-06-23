package com.meltingb.medicare.core

interface BaseResponse<T> {

    fun onSuccess(data: T)

    fun onFail(descriptor: String)

    fun onError(throwable: Throwable)

    fun onLoading()

    fun onLoaded()
}