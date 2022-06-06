package com.example.indikascam.api.responses

data class GetMyReportsResponse(
    val data: List<Data>
) {
    data class Data(
        val id: Int,
        val report_number: String,
        val created_at: String,
        val report_type: String,
        val status: String
    )
}