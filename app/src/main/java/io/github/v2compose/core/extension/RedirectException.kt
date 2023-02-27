package io.github.v2compose.core.extension

import retrofit2.HttpException

fun Exception.isRedirect(location: String): Boolean {
    return this is HttpException && code().isRedirect
            && response()?.raw()?.header("location") == location
}

val Exception.isRedirect
    get(): Boolean {
        return this is HttpException && code().isRedirect
    }

val Exception.redirectLocation
    get(): String? {
        return if (this is HttpException && code().isRedirect) {
            response()?.raw()?.header("location")
        } else null
    }