package com.example.indikascam.api.responses

data class GetScamStatisticsResponse(
    val data: Data
) {
    data class Data(
        val total_loss: String,
        val platforms: List<Platform>,
        val products: List<Product>
    ) {
        data class Platform(
            val name: String,
            val total: Int
        )

        data class Product(
            val name: String,
            val total: Int
        )
    }
}