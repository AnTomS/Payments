package com.atom.payments.data.repository

import android.util.Log
import com.atom.payments.data.dto.LoginRequest
import com.atom.payments.data.dto.Payment
import com.atom.payments.data.dto.TokenResponse
import com.atom.payments.data.network.ApiServiceInterface
import com.atom.payments.data.network.UserManager


class Repository(private val apiService: ApiServiceInterface) {
    suspend fun login(login: String, password: String): TokenResponse {
        try {
            val loginRequest = LoginRequest(login, password)
            val response = apiService.login(loginRequest)
            if (response.success) {
                val token = response.response?.token
                if (!token.isNullOrBlank()) {
                    Log.d("Repository", "Login successful. Received token: $token")
                    return response
                } else {
                    Log.e("Repository", "Token is null or blank in the response")
                }
            } else {
                Log.e("Repository", "Login failed. Server response: ${response.toString()}")
            }
        } catch (e: Exception) {
            Log.e("Repository", "Login failed. Error: ${e.message}")
            throw e
        }
        throw Exception("Login failed. Unexpected server response.")
    }

    suspend fun getPayments(): List<Payment> {
        val token = UserManager.token
        if (token != null) {
            try {
                val response = apiService.getPayments(token)
                if (response.success) { // Проверяем свойство success
                    val payments = response.response ?: emptyList()
                    Log.d("Repository", "Платежи успешно получены")
                    return payments
                } else {
                    Log.e(
                        "Repository",
                        "Не удалось получить платежи. Ответ сервера: ${response.toString()}"
                    )
                    throw Exception("Не удалось получить платежи")
                }
            } catch (e: Exception) {
                Log.e("Repository", "Не удалось получить платежи. Ошибка: ${e.message}")
                throw e
            }
        } else {
            Log.e("Repository", "Пользователь не авторизован")
            throw Exception("Пользователь не авторизован")
        }
    }
}

