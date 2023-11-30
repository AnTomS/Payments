package com.atom.payments.domain.dto


import com.google.gson.annotations.SerializedName

data class Payments(
    @SerializedName("response")
    val response: List<Payment>,
    @SerializedName("success")
    val success: Boolean,
)