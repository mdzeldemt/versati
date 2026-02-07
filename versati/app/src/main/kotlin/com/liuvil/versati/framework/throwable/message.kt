package com.liuvil.versati.framework.throwable

import retrofit2.HttpException

val Throwable.detailedMessage: String?
    get() =
        when (this) {
            is HttpException ->
                response()
                    ?.errorBody()
                    ?.string()

            else ->
                message
        }