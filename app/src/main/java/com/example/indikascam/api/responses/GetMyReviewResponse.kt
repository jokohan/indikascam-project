package com.example.indikascam.api.responses

data class GetMyReviewResponse(
    val data: List<Data>
) {
    data class Data(
        val id: Int,
        val user_id: Int,
        val report_number: String,
        val created_at: String,
        val report_type: String,
        val status: String
    )
}