package com.plcoding.cryptotracker.crypto.core.data.networking

import com.plcoding.cryptotracker.crypto.core.domain.util.NetworkError
import com.plcoding.cryptotracker.crypto.core.domain.util.Result.*
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.SerializationException
import kotlin.coroutines.coroutineContext

suspend inline fun <reified T> safeCall(
    execute: () -> HttpResponse
): com.plcoding.cryptotracker.crypto.core.domain.util.Result<T, NetworkError> {
    val response =  try {
        execute()
    }  catch(e: UnresolvedAddressException) {
        return Error(NetworkError.NO_INTERNET)
    } catch(e: SerializationException) {
        return Error(NetworkError.SERIALIZATION)
    } catch(e: Exception) {
        // This one is in case some error regarding coroutines happens
        // it is not swallowed by the UNKNOWN network error exception
        coroutineContext.ensureActive()
        return Error(NetworkError.UNKNOWN)
    }

    return responseToResult(response)
}