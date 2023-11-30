package com.atom.payments.data.network

import com.atom.payments.domain.dto.LoginRequest
import com.atom.payments.domain.dto.Payments
import com.atom.payments.domain.dto.TokenResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiServiceInterface {

    /*
     Базовый URL https://easypay.world/api-test/, в заголовках надо передавать app-key=12345 и v=1

    POST /login - логин

    GET /payments - список платежей, для корректного запроса в заголовках передаем ранее полученный токен token='...'

    */


    @Headers("app-key: 12345", "v: 1")
    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): TokenResponse

    @Headers("app-key: 12345", "v: 1")
    @GET("payments")
    suspend fun getPayments(@Header("token") token: String): Payments
}