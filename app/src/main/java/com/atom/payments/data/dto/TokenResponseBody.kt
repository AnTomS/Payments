package com.atom.payments.data.dto

import com.google.gson.annotations.SerializedName

data class TokenResponseBody(
    @SerializedName("token")
    val token: String
)
