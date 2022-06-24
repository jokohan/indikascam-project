package com.example.indikascam.modelsRcv

import android.net.Uri

data class Proof (
    val image: Uri?,
    val title: String,
    val isItImage: Boolean
    )