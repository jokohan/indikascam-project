package com.example.indikascam.modelsRcv

import android.graphics.Bitmap
import java.util.*

data class ReportHistory(
    val id: Int,
    val image: Bitmap?,
    val username: String,
    val reportType: String,
    val date: Date,
    val reportStatus: String,
    val bankId: Int?,
    val bankName: String?
)