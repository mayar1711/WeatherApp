package com.example.temptrack.util


sealed class ResultCallBack <out T>{
    data class Success<out T>(val data :T) : ResultCallBack<T>()
    data class Error(val message: Throwable) : ResultCallBack<Nothing>()
    object Loading: ResultCallBack<Nothing>()
}