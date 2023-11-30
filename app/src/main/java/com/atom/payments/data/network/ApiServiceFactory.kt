package com.atom.payments.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiServiceFactory {
    private const val BASE_URL = "https://easypay.world/api-test/"

    val apiService: ApiServiceInterface by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiServiceInterface::class.java)
    }
}


object UserManager {
    var token: String? = null


    fun saveToken(token: String) {
        this.token = token

    }

    fun clearToken() {
        this.token = null
    }
}