package com.androidfactory.network

import id.oversteken.models.LocationTimer
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.util.InternalAPI


class KtorClient(private val baseUrl: String) {
    private val client = HttpClient(OkHttp) {
    }


    private inline fun <T> safeApiCall(apiCall: () -> T): ApiOperation<T> {
        return try {
            ApiOperation.Success(data = apiCall())
        } catch (e: Exception) {
            ApiOperation.Failure(exception = e)
        }
    }

    @OptIn(InternalAPI::class)
    suspend fun sendMessage(message: String): ApiOperation<HttpResponse> {

       return safeApiCall {
            client.post(baseUrl) {
                header("Content-Type", "application/json")
                body = message
            }
        }
    }
    @OptIn(InternalAPI::class)
    suspend fun stopMessage(): ApiOperation<HttpResponse> {
        return safeApiCall {
            client.post("http://10.0.2.2:8080/stop") {
                header("Content-Type", "application/json")
                body = "stop"
            }
        }
    }

    sealed interface ApiOperation<T> {
        data class Success<T>(val data: T) : ApiOperation<T>
        data class Failure<T>(val exception: Exception) : ApiOperation<T>

        fun <R> mapSuccess(transform: (T) -> R): ApiOperation<R> {
            return when (this) {
                is Success -> Success(transform(data))
                is Failure -> Failure(exception)
            }
        }

        fun onSuccess(block: (T) -> Unit): ApiOperation<T> {
            if (this is Success) block(data)
            return this
        }

        fun onFailure(block: (Exception) -> Unit): ApiOperation<T> {
            if (this is Failure) block(exception)
            return this
        }
    }}