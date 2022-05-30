package com.example.indikascam.modelsRcv

import java.util.*

data class ReportHistory(
    val image: Int,
    val username: String,
    val reportType: String,
    val date: Date,
    val reportStatus: String
)