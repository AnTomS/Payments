package com.atom.payments.domain.dto

import com.google.gson.annotations.SerializedName


data class TokenResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("response")
    val response: TokenResponseBody?
)
