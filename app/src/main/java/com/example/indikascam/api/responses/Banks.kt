package com.example.indikascam.api.responses

data class Banks(
    val data: List<Data>
) {
    data class Data(
        val id: Int,
        val name: String,
        val active: Int
    )
}