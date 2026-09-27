package com.steady.app.core.network

import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.Interceptor
import okhttp3.Response

@Singleton
class ErrorInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = try {
        chain.proceed(chain.request())
    } catch (exception: IOException) {
        throw NetworkConnectionException(exception)
    }
}

class NetworkConnectionException(cause: IOException) : IOException("Unable to reach server.", cause)
