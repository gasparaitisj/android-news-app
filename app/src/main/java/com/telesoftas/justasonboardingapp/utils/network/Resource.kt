package com.telesoftas.justasonboardingapp.utils.network

import androidx.annotation.StringRes

data class Resource<out T>(val resourceStatus: ResourceStatus, val data: T?, val message: String?, @StringRes val messageRes: Int?) {
    companion object {
        fun <T> success(data: T? = null): Resource<T> {
            return Resource(ResourceStatus.SUCCESS, data, null, null)
        }

        fun <T> error(msg: String? = null, @StringRes msgRes: Int? = null, data: T? = null): Resource<T> {
            return Resource(ResourceStatus.ERROR, data, msg, msgRes)
        }

        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(ResourceStatus.LOADING, data, null, null)
        }
    }
    fun getSuccessDataOrNull(): T? = if (resourceStatus == ResourceStatus.SUCCESS) data else null
}

enum class ResourceStatus {
    SUCCESS,
    ERROR,
    LOADING
}
