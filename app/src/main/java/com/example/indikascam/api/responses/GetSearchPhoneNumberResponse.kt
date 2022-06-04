package com.example.indikascam.api.responses

data class GetSearchPhoneNumberResponse(
    val data: Data
) {
    data class Data(
        val is_blocked: Boolean,
        val user_reports: List<UserReport>
    ) {
        data class UserReport(
            val id: Int,
            val reporter_id: Int,
            val reporter_name: String,
            val profile_picture: String?,
            val report_type: String,
            val created_at: String,
            val status: String
        )
    }
}