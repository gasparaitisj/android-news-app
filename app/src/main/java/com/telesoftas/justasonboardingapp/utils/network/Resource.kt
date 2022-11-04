package com.telesoftas.justasonboardingapp.utils.network

import androidx.annotation.StringRes

data class Resource<out T>(val status: Status, val data: T?, val message: String?, @StringRes val messageRes: Int?) {
    companion object {
        fun <T> success(data: T? = null): Resource<T> {
            return Resource(Status.SUCCESS, data, null, null)
        }

        fun <T> error(msg: String? = null, @StringRes msgRes: Int? = null, data: T? = null): Resource<T> {
            return Resource(Status.ERROR, data, msg, msgRes)
        }
    }
    fun getSuccessDataOrNull(): T? = if (status == Status.SUCCESS) data else null
}

enum class Status {
    SUCCESS,
    ERROR
}
