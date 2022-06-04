package com.example.indikascam.api.responses

data class PlatformsAndReportTypeResponse(
    val data: List<Data>
) {
    data class Data(
        val id: Int,
        val name: String
    )
}