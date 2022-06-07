package com.example.indikascam.api.responses

data class GetBlockStatisticsResponse(
    val `data`: Data
) {
    data class Data(
        val total: Int,
        val this_month: Int,
        val this_week: Int,
        val last_4_months: List<Float>,
        val pie_chart: PieChart
    ) {
        data class PieChart(
            val penipuan: Float,
            val panggilan_spam: Float,
            val panggilan_robot: Float
        )
    }
}