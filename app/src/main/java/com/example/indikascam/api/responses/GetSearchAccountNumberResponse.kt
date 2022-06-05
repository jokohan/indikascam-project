package com.example.indikascam.api.responses

data class GetSearchAccountNumberResponse(
    val data: List<Data>
) {
    data class Data(
        val id: Int,
        val reporter_id: Int,
        val reporter_name: String,
        val profile_picture: String?,
        val report_type: String,
        val created_at: String,
        val status: String
    )
}