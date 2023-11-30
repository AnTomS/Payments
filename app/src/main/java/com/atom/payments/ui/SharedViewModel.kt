package com.atom.payments.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atom.payments.data.dto.Payment
import com.atom.payments.data.network.UserManager
import com.atom.payments.data.repository.Repository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class SharedViewModel(private val repository: Repository) : ViewModel() {

    sealed class PaymentsResult {
        data class Success(val payments: List<Payment>) : PaymentsResult()
        data class Error(val errorMessage: String) : PaymentsResult()
    }


    sealed class LoginResult {
        data class Success(val login: String, val password: String) : LoginResult()
        object InvalidLogin : LoginResult()
        object InvalidPassword : LoginResult()
        data class Error(val errorCode: Int, val errorMessage: String) : LoginResult()
    }

    sealed class TokenResult {
        data class Success(val token: String?) : TokenResult()
        data class Error(val errorMessage: String) : TokenResult()
    }

    private val _paymentsResult = MutableLiveData<PaymentsResult>()
    val paymentsResult: LiveData<PaymentsResult> get() = _paymentsResult

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> get() = _loginResult

    private val _tokenResult = MutableLiveData<TokenResult>()
    val tokenResult: LiveData<TokenResult> get() = _tokenResult

    fun login(login: String, password: String) {
        viewModelScope.launch {
            if (login.isBlank()) {
                _loginResult.value = LoginResult.InvalidLogin
                return@launch
            }

            if (password.isBlank()) {
                _loginResult.value = LoginResult.InvalidPassword
                return@launch
            }

            try {
                Log.d("LoginFragment", "Attempting login with login: $login, password: $password")
                val response = repository.login(login, password)

                if (response.success) {
                    _loginResult.value = LoginResult.Success(login, password)
                    UserManager.saveToken(response.response?.token ?: "")
                } else {
                    _loginResult.value = LoginResult.Error(1003,"Неверный логин или пароль")
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unknown error"
                Log.e("LoginFragment", "Login failed. Error: $errorMessage")
                _loginResult.value = LoginResult.Error(1003,"Неверный логин или пароль")
            }
        }
    }

    fun getToken(login: String, password: String) {
        viewModelScope.launch {
            try {
                val response = async { repository.login(login, password) }.await()
                val token = response.response?.token
                Log.d("SharedViewModel", "Received token: $token")
                _tokenResult.value = TokenResult.Success(response.response?.token)
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unknown error"
                Log.e("SharedViewModel", "Token retrieval error: $errorMessage")
                _tokenResult.value = TokenResult.Error(errorMessage)
            }
        }
    }

    fun logout() {
        UserManager.clearToken()
    }

    fun getPayments() {
        viewModelScope.launch {
            try {
                val result = async { repository.getPayments() }
                val payments = result.await()
                _paymentsResult.value = PaymentsResult.Success(payments)
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unknown error"
                _paymentsResult.value = PaymentsResult.Error(errorMessage)
            }
        }
    }


    fun isLoginValid(login: String): Boolean {
        return login.isNotBlank()
    }

    fun isPasswordValid(password: String): Boolean {
        return password.isNotBlank()
    }

    fun validateLoginInput(login: String): Boolean {
        return isLoginValid(login)
    }

    fun validatePasswordInput(password: String): Boolean {
        return isPasswordValid(password)
    }
}


