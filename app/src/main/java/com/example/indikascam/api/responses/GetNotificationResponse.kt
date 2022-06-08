package com.example.indikascam.api.responses

data class GetNotificationResponse(
    val today: List<Today>,
    val this_month: List<ThisMonth>
) {
    data class Today(
        val id: Int,
        val message: String
    )

    data class ThisMonth(
        val id: Int,
        val message: String
    )
}