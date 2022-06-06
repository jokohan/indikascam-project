package com.example.indikascam.api.responses

data class PostReviewRequestResponse(
    val message: String,
    val data: Data
) {
    data class Data(
        val review_request: ReviewRequest
    ) {
        data class ReviewRequest(
            val user_report_id: String,
            val user_id: Int,
            val updated_at: String,
            val created_at: String,
            val id: Int
        )
    }
}