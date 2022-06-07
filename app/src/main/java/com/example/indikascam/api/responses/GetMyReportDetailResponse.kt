package com.example.indikascam.api.responses

data class GetMyReportDetailResponse(
    val data: Data
) {
    data class Data(
        val id: Int,
        val report_number: String,
        val created_at: String,
        val report_type: String,
        val status: String,
        val scammer_name: String?,
        val scammer_phone_number: String,
        val bank_account_number: String?,
        val bank_name: String?,
        val platform_name: String?,
        val product_name: String?,
        val total_loss: Int?,
        val chronology: String?,
        val files: List<File>
    ) {
        data class File(
            val client_file_name: String,
            val server_file_name: String
        )
    }
}