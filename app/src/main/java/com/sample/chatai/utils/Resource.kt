package com.sample.chatai.utils

sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()

    class Error<T>(val exception: Exception) : Resource<T>()
}