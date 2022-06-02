package com.example.indikascam.api.responses

data class BankNameById(
    val data: Data
) {
    data class Data(
        val id: Int,
        val name: String,
        val active: Int,
        val created_at: String,
        val updated_at: String
    )
}