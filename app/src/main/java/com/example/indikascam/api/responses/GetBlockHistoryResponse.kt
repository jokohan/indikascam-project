package com.example.indikascam.api.responses

data class GetBlockHistoryResponse(
    val data: List<Data>
) {
    data class Data(
        val id: Int,
        val user_id: Int,
        val phone_number: String,
        val is_blocked: Int,
        val block_status: String,
        val is_automatic: Int,
        val created_at: String
    )
}