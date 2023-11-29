package com.atom.payments.data.dto


import com.google.gson.annotations.SerializedName

data class Payment(
    @SerializedName("amount")
    val amount: String?,
    @SerializedName("created")
    val created: Int?,
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String
)