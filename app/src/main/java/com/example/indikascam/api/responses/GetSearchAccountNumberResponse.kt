package com.example.indikascam.api.responses

data class GetSearchAccountNumberResponse(
    val data: List<Data>
) {
    data class Data(
        val id: Int,
        val reporter_id: Int,
        val reporter_name: String,
        val is_anonymous: Int,
        val report_type: String,
        val created_at: String,
        val status: String,
        val files: List<File>
    ) {
        data class File(
            val client_file_name: String,
            val server_file_name: String
        )
    }
}