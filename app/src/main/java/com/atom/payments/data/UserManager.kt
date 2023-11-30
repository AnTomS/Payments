package com.atom.payments.data

object UserManager {
    var token: String? = null


    fun saveToken(token: String) {
        this.token = token

    }

    fun clearToken() {
        this.token = null
    }
}